package org.wsp.mybookshelf.domain.review.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.domain.review.dto.BookReviewDTO;
import org.wsp.mybookshelf.domain.review.service.BookReviewService;
import org.wsp.mybookshelf.global.response.ApiResponse;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class BookReviewController {

    private final BookReviewService reviewService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BookReviewDTO>> createReview(
            @RequestBody BookReviewDTO dto, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.onFailure("401", "로그인이 필요합니다."));
        }

        dto.setUserId((Long) session.getAttribute("userId"));
        return ResponseEntity.ok(ApiResponse.onSuccess(reviewService.createReview(dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookReviewDTO>> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.onSuccess(reviewService.getReview(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookReviewDTO>> updateReview(
            @PathVariable Long id,
            @RequestBody BookReviewDTO dto,
            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.onFailure("401", "로그인이 필요합니다."));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(reviewService.updateReview(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.onFailure("401", "로그인이 필요합니다."));
        }

        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.onSuccess("리뷰 삭제 성공"));
    }
}
