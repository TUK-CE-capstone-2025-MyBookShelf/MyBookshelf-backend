package org.wsp.mybookshelf.domain.OpenAI;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.domain.book.dto.BookCandidateDTO;
import org.wsp.mybookshelf.domain.book.dto.BookKeywordResponse;
import org.wsp.mybookshelf.domain.book.dto.BookScoreDTO;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final OpenAIClient openAIClient;
    private final AladinService aladinService;

    // 키워드 누락 시 GPT로 보완
    public List<BookKeywordResponse.Keyword> generateMissingKeywords(String isbn, int missingCount) {
        BookResponse bookInfo = aladinService.searchBookDetail(isbn);
        if (bookInfo == null) return Collections.emptyList();

        String prompt = buildKeywordPrompt(bookInfo, missingCount);
        String gptResponse = openAIClient.getCompletion(prompt, "gpt-3.5-turbo");  // 유지: 보완은 3.5로도 충분
        List<String> gptKeywords = parseKeywordList(gptResponse);

        List<BookKeywordResponse.Keyword> generated = new ArrayList<>();
        double weight = 7.0;

        for (String keyword : gptKeywords) {
            if (keyword.trim().isEmpty()) continue;
            BookKeywordResponse.Keyword kw = new BookKeywordResponse.Keyword();
            kw.setWord(keyword.trim());
            kw.setWeight(weight);
            generated.add(kw);
            weight = Math.max(weight - 1.0, 3.0);
        }

        return generated;
    }

    private String buildKeywordPrompt(BookResponse bookInfo, int missingCount) {
        return """
            다음 도서 정보를 참고하여 핵심 키워드 %d개를 추천해줘.
            도서 제목: %s
            도서 설명: %s
            도서 카테고리: %s
            조건: 모든 키워드는 2~3글자로 간결하고, 의미가 명확한 단어로 생성해줘.
            출력 형식: 키워드1, 키워드2, ..., 키워드%d
            """.formatted(missingCount, bookInfo.getTitle(), bookInfo.getDescription(), bookInfo.getCategoryName(), missingCount);
    }


    private List<String> parseKeywordList(String gptResponse) {
        return Arrays.asList(gptResponse.split(",\\s*"));
    }

    public String buildSimilarityPrompt(List<String> keywords, List<BookScoreDTO> candidates) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 도서 추천 AI입니다.\n");
        sb.append("다음은 사용자가 선호한 도서 키워드입니다:\n");
        sb.append(String.join(", ", keywords)).append("\n\n");

        sb.append("후보 도서 목록이 아래에 있습니다.\n");
        sb.append("각 도서의 ISBN만 보고, 사용자 키워드와 관련된 정도를 6~10점으로 평가해 주세요.\n");
        sb.append("❗❗ 반드시 형식은 'ISBN=점수' 로만 출력하고, 다른 설명은 절대 쓰지 마세요.\n");
        sb.append("예시:\n");
        sb.append("9788956608556=7\n");
        sb.append("K742038673=9\n\n");

        sb.append("후보 도서 목록:\n");
        for (BookScoreDTO book : candidates) {
            sb.append(book.getIsbn()).append("\n");
        }

        return sb.toString();
    }

    // ✅ GPT-4 Turbo로 변경
    public Map<String, Double> getKeywordSimilarityScore(String prompt) {
        String response = openAIClient.getCompletion(prompt, "gpt-4-turbo");  // 수정됨

        Map<String, Double> scoreMap = new HashMap<>();
        String[] lines = response.split("\\n");

        for (String line : lines) {
            if (line.matches(".*[=|:].*\\d+(\\.\\d+)?")) {
                String isbn = null;
                double score = 0.0;

                if (line.contains("ISBN:")) {
                    int idx = line.indexOf("ISBN:");
                    isbn = line.substring(idx + 5).split("[ =]")[0].trim();
                } else if (line.contains("=")) {
                    isbn = line.split("=")[0].trim();
                }

                try {
                    String scoreStr = line.substring(line.lastIndexOf('=') + 1).trim();
                    score = Double.parseDouble(scoreStr);
                } catch (Exception e) {
                    System.err.println("❌ 점수 파싱 실패: " + line);
                    continue;
                }

                if (isbn != null && !isbn.isEmpty()) {
                    scoreMap.put(isbn, score);
                }
            }
        }

        return scoreMap;
    }

    public String getCompletionForRecommendation(String prompt) {
        return openAIClient.getCompletion(prompt, "gpt-4-turbo");
    }

    public String getCompletionForKeyword(String prompt) {
        return openAIClient.getCompletion(prompt, "gpt-3.5-turbo");
    }
}
