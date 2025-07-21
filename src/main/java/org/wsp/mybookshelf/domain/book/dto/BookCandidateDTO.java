package org.wsp.mybookshelf.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// BookCandidateDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCandidateDTO {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private String categoryName;
    private Integer categoryId;

}