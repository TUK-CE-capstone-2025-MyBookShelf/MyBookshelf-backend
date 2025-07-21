package org.wsp.mybookshelf.global.searchApi.service;

import org.springframework.http.*;
import org.wsp.mybookshelf.domain.book.dto.BookCandidateDTO;
import org.wsp.mybookshelf.domain.book.dto.BookKeywordResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final RestTemplate restTemplate;
    private static final String API_KEY = "22f142f8a1be2fe8871c7c10227c2d96ea0c115af60850f3c088409abfd3286c";
    private static final String BASE_URL = "https://data4library.kr/api";

    public LibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BookResponse> searchBooks(String query) {
        String url = BASE_URL + "/srchDtlList?authKey=" + API_KEY + "&isbn13=" + query + "&format=json";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<BookResponse> books = new ArrayList<>();

        if (response != null && response.containsKey("response")) {
            Map<String, Object> responseData = (Map<String, Object>) response.get("response");

            if (responseData.containsKey("docs")) {
                List<Map<String, Object>> docs = (List<Map<String, Object>>) responseData.get("docs");

                for (Map<String, Object> doc : docs) {
                    Map<String, Object> bookData = (Map<String, Object>) doc.get("doc");

                    BookResponse book = new BookResponse();
                    book.setTitle((String) bookData.get("bookname"));
                    book.setAuthor((String) bookData.get("authors"));
                    book.setPublisher((String) bookData.get("publisher"));
                    book.setIsbn((String) bookData.get("isbn13"));
                    book.setSource("Library API");
                    books.add(book);
                }
            }
        }
        return books;
    }

    public List<BookCandidateDTO> searchBooksByKeyword(String keyword, int limit) {
        String url = BASE_URL + "/srchBooks?authKey=" + API_KEY +
                "&keyword=" + keyword + "&pageSize=" + limit + "&format=json"; // ❌ 인코딩 제거!

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        List<BookCandidateDTO> books = new ArrayList<>();

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("response");
            if (responseData != null && responseData.containsKey("docs")) {
                List<Map<String, Object>> docs = (List<Map<String, Object>>) responseData.get("docs");
                for (Map<String, Object> doc : docs) {
                    Map<String, Object> bookData = (Map<String, Object>) doc.get("doc");
                    BookCandidateDTO book = new BookCandidateDTO();
                    book.setTitle((String) bookData.get("bookname"));
                    book.setAuthor((String) bookData.get("authors"));
                    book.setPublisher((String) bookData.get("publisher"));
                    book.setIsbn((String) bookData.get("isbn13"));
                    books.add(book);
                }
            }
        }
        return books;
    }





    public BookKeywordResponse getBookKeywords(String isbn) {
        String url = BASE_URL + "/keywordList?authKey=" + API_KEY + "&isbn13=" + isbn + "&additionalYN=Y&format=json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*"); // 모든 타입 허용
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        // 1️⃣ API 응답이 없는 경우 예외 처리
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            System.out.println("❌ API 응답 없음: " + response.getStatusCode());
            return null; // 책 정보를 가져올 방법이 없으므로 null 반환
        }

        Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("response");

        // 2️⃣ API에서 response 필드 자체가 없을 경우
        if (responseData == null) {
            System.out.println("❌ API 응답에 'response' 필드 없음");
            return null;
        }

        // 3️⃣ 추가 정보 가져오기 (책 정보 유지)
        Map<String, Object> additionalItem = (Map<String, Object>) responseData.get("additionalItem");
        if (additionalItem == null) {
            System.out.println("❌ 추가 정보 없음");
            return null;
        }

        BookKeywordResponse bookResponse = new BookKeywordResponse();
        bookResponse.setBookname((String) additionalItem.get("bookname"));

        bookResponse.setIsbn13((String) additionalItem.get("isbn13"));

        // 4️⃣ 키워드 리스트 처리
        List<BookKeywordResponse.Keyword> keywordList = new ArrayList<>();

        // "items" 필드가 없으면 빈 리스트로 유지
        if (responseData.containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) responseData.get("items");

            for (Map<String, Object> itemWrapper : items) {
                Map<String, Object> item = (Map<String, Object>) itemWrapper.get("item"); // 중첩된 'item' 접근
                if (item != null) {
                    String word = (String) item.get("word");
                    if (word == null || word.trim().isEmpty()) {
                        continue; // 빈 키워드는 제외
                    }

                    BookKeywordResponse.Keyword keyword = new BookKeywordResponse.Keyword();
                    keyword.setWord(word);

                    // weight 값이 null이면 기본값 0.0 설정
                    Object weightObj = item.get("weight");
                    keyword.setWeight(weightObj != null ? Double.parseDouble(weightObj.toString()) : 0.0);

                    keywordList.add(keyword);
                }
            }
        }

        // 5️⃣ 키워드 개수 조정
        if (keywordList.size() > 10) {
            keywordList = keywordList.stream()
                    .sorted(Comparator.comparingDouble(BookKeywordResponse.Keyword::getWeight).reversed()) // 내림차순 정렬
                    .limit(10) // 상위 10개 선택
                    .collect(Collectors.toList());
        } else if (keywordList.isEmpty()) {
            System.out.println("⚠️ 키워드 없음, 빈 리스트 반환 (GPT 보완 필요)");
        }

        bookResponse.setKeywords(keywordList);
        return bookResponse;
    }

}
