package org.wsp.mybookshelf.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookRecommendDTO {
    private String title;
    private String author;
    private String isbn;
    private double weightedRatingScore;
    private String cover;
    private String categoryName;
    private Integer categoryId;
}
