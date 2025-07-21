package org.wsp.mybookshelf.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDTO {
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String publicationDate;
    private String description;
    private String cover; // 책 표지
    private Integer customerReviewRank;
    private String source;  // 데이터 출처
    private Integer categoryId;
    private String categoryName;

}