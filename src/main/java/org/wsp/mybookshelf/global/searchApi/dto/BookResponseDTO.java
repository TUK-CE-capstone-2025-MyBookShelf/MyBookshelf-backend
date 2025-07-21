package org.wsp.mybookshelf.global.searchApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponseDTO {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String cover;

    @JsonProperty("customerReviewRank")  // JSON 필드명을 Java 필드명과 매칭
    private Integer customerReviewRank;

    private String source;
}
