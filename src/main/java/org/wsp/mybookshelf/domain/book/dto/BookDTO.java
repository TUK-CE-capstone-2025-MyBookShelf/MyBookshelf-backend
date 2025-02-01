package org.wsp.mybookshelf.domain.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDTO {
    private Long bookId;
    private String title;
    private String isbn;
    private String thumbnail;

    @Getter
    @AllArgsConstructor
    public static class BookDetailDTO {
        private String bookTitle;     // 책 제목
        private String author;        // 저자
        private String publisher;      // 출판사
        private String isbn;          // ISBN 번호
        private String category;       // 카테고리
        private String thumbnail;      // 책 표지 이미지 URL
        private String description;    // 책 설명
        private Integer page;          // 페이지 수
    }
}