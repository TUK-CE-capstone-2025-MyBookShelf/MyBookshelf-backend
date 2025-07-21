package org.wsp.mybookshelf.domain.bookshelf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.wsp.mybookshelf.domain.book.dto.BookDTO;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookShelfDTO {
    private Long bookshelfId;
    private String bookshelfName;
    private List<BookDTO> book;


    @Getter
    @AllArgsConstructor
    public static class CreateBookShelfDTO { // static으로 선언
        private Long userId;          // 사용자 ID
        private String bookshelfName; // 책장 이름
    }

    // 책장 이름 변경 DTO
    @Getter
    @AllArgsConstructor
    public static class UpdateBookShelfDTO {
        private Long bookshelfId;
        private String newBookshelfName;
    }
}