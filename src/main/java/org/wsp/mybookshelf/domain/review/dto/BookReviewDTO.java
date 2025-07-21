package org.wsp.mybookshelf.domain.review.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookReviewDTO {
    private Long reviewId;
    private Long userId;
    private Long bookId;
    private String title;
    private String content;
}
