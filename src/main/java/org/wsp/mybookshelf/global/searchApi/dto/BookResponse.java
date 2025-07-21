package org.wsp.mybookshelf.global.searchApi.dto;

import lombok.Data;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

@Getter
@Data
public class BookResponse {
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
