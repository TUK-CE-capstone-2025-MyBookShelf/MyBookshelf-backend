package org.wsp.mybookshelf.domain.recommendation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.wsp.mybookshelf.domain.book.dto.BookCandidateDTO;
import org.wsp.mybookshelf.domain.book.dto.BookKeywordResponse;
import org.wsp.mybookshelf.domain.book.dto.BookRecommendDTO;
import org.wsp.mybookshelf.domain.book.dto.BookScoreDTO;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.domain.recommendation.service.CategoryRecommendService;
import org.wsp.mybookshelf.domain.recommendation.service.RecommendationService;
import org.wsp.mybookshelf.global.response.ApiResponse;
import org.wsp.mybookshelf.global.searchApi.service.LibraryService;

import java.util.*;
@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private BookShelfService bookShelfService;

    @Autowired
    private CategoryRecommendService categoryRecommendService;

    // 평점 기반 추천
    @GetMapping("/rating/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<BookRecommendDTO>>> recommendByRating(@PathVariable Long bookshelfId) {
        List<BookRecommendDTO> result = recommendationService.getRatingBasedCandidates(bookshelfId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result.stream().limit(5).toList()));
    }

    // 키워드 기반 추천
    @GetMapping("/keyword/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<BookRecommendDTO>>> recommendByKeyword(@PathVariable Long bookshelfId) {
        List<BookRecommendDTO> result = recommendationService.getKeywordBasedCandidates(bookshelfId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

//    @GetMapping("/ratingCategory/{bookshelfId}")
//    public ResponseEntity<List<Map<String, Object>>> getRecommendedByBookshelf(@PathVariable Long bookshelfId) {
//        System.out.println("[API 요청] 추천 도서 조회 - 책장 ID: " + bookshelfId);
//        List<Map<String, Object>> recommendedBooks = bookShelfService.getRecommendedBooksByBookshelfId(bookshelfId);
//
//        System.out.println("[API 응답] 추천 도서 개수: " + recommendedBooks.size());
//        return ResponseEntity.ok(recommendedBooks);
//    }
    
    //통합 추천
    @GetMapping("/total/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<BookScoreDTO>>> getTotalRecommendation(@PathVariable Long bookshelfId) {
        // 1. 추천 후보 도서 통합
        List<BookRecommendDTO> totalCandidates = recommendationService.getCombinedRecommendationCandidatesTest(bookshelfId);


        List<BookScoreDTO> bookScore = recommendationService.applyWeightedRatingToBooksForTest(totalCandidates);

        bookScore = recommendationService.applyCategoryInfo(bookScore);
        // 3. 베스트셀러 점수 적용
        bookScore = recommendationService.applyBestsellerScore(bookScore);

        // 4. 중복 점수 적용
        //bookScore = recommendationService.applyDuplicateScore(bookScore, totalCandidates);

        // ✅ 5. 장르 유사도 점수 적용 (categoryId 기준)
        List<Integer> category = bookShelfService.getCategoryIdsFromBookshelf(bookshelfId);

        bookScore = recommendationService.applyGenreSimilarityScore(bookScore,category);
        
        //사용자 선호
        bookScore = recommendationService.applyPreferenceScore(bookScore, bookshelfId); // ✅ 선호 장르 점수 계산


        // ✅ 6. 책장에 등록된 도서 키워드 수집
        List<String> userIsbns = bookShelfService.getIsbn(bookshelfId);
        List<String> allUserKeywords = recommendationService.getAllKeywordsFromBooks(userIsbns);

        // ✅ 7. 키워드 유사도 점수 적용
        bookScore = recommendationService.applyKeywordSimilarityScore(bookScore, allUserKeywords);

        // 8. 최종 totalScore 계산
        bookScore = recommendationService.calculateTotalScore(bookScore);

        // 9. 상위 5권만 반환
        List<BookScoreDTO> top5Books = bookScore.stream()
                .sorted(Comparator.comparingDouble(BookScoreDTO::getTotalScore).reversed())
                .limit(100)
                .toList();

        top5Books = recommendationService.applyMissingBookInfo(top5Books);

        return ResponseEntity.ok(ApiResponse.onSuccess(top5Books));
    }

    @GetMapping("/test/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<BookScoreDTO>>> forTestRecommendation(@PathVariable Long bookshelfId) {
        List<BookRecommendDTO> testCandidates = recommendationService.getCombinedRecommendationCandidatesTest(bookshelfId);

        List<BookScoreDTO> ratedBooks = recommendationService.applyWeightedRatingToBooksForTest(testCandidates);

        // 3. 베스트셀러 점수 적용
        ratedBooks = recommendationService.applyBestsellerScore(ratedBooks);

        // 4. 중복 점수 적용
        //ratedBooks = recommendationService.applyDuplicateScore(ratedBooks, testCandidates);

        // 장르 유사도 점수 적용 (categoryId 기준)
        //ratedBooks = recommendationService.applyGenreSimilarityScore(ratedBooks);
        //ratedBooks = recommendationService.applyDummyGenreAndPreferenceScore(ratedBooks);

        // ✅ 5. 책장에 등록된 도서 키워드 수집
        List<String> userIsbns = bookShelfService.getIsbn(bookshelfId);
        List<String> allUserKeywords = recommendationService.getAllKeywordsFromBooks(userIsbns);

        // ✅ 6. 키워드 유사도 점수 적용 (OpenAI로 평가)
        //ratedBooks = recommendationService.applyKeywordSimilarityScore(ratedBooks, allUserKeywords);



        // 7. 최종 totalScore 계산
        ratedBooks = recommendationService.calculateTotalScore(ratedBooks);

        // 8. 상위 10권만 반환
        List<BookScoreDTO> top10Books = ratedBooks.stream()
                .sorted(Comparator.comparingDouble(BookScoreDTO::getTotalScore).reversed())
                .limit(5)
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(top10Books));
    }


    //isbn으로 키워드 기반 추천
    @GetMapping("/keywords/book/{isbn}")
    public ResponseEntity<BookKeywordResponse> getBookKeywords(@PathVariable String isbn) {
        BookKeywordResponse response = libraryService.getBookKeywords(isbn);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

//    // 카테고리 기반
//    @GetMapping("/categoryByID/{bookshelfId}")
//    public ResponseEntity<ApiResponse<List<BookRecommendDTO>>> recommendByCategory(@PathVariable Long bookshelfId) {
//        List<BookRecommendDTO> result = recommendationService.getCategoryBasedCandidates(bookshelfId);
//        return ResponseEntity.ok(ApiResponse.onSuccess(result));
//    }

    @GetMapping("/{userId}/genre")
    public ResponseEntity<ApiResponse<List<BookScoreDTO>>> recommendByUserPreferredGenres(@PathVariable Long userId) {
        List<BookScoreDTO> results = recommendationService.recommendByUserPreferredGenres(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(results));
    }


}

