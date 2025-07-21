package org.wsp.mybookshelf.domain.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookKeywordResponse {
    private String bookname;
    private String isbn13;
    private List<Keyword> keywords;

    @Getter
    @Setter
    public static class Keyword {
        private String word;
        private double weight;
    }
}