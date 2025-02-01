package org.wsp.mybookshelf.domain.bookshelf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.domain.book.dto.BookDTO;
import org.wsp.mybookshelf.domain.bookshelf.dto.BookShelfDTO;
import org.wsp.mybookshelf.domain.bookshelf.entity.BookShelf;
import org.wsp.mybookshelf.domain.bookshelf.service.BookShelfService;
import org.wsp.mybookshelf.global.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/bookshelf")
public class BookShelfController {

    @Autowired
    private BookShelfService bookShelfService;

    //사용자 ID로 책장 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<BookShelfDTO>>> getBookshelves(@PathVariable Long userId) {
        List<BookShelfDTO> bookShelfList = bookShelfService.getBookShelf(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(bookShelfList));
    }

    @PostMapping("/{bookshelfId}/book")
    public ResponseEntity<ApiResponse<String>> addBookToBookshelf(@PathVariable Long bookshelfId,
                                                                  @RequestBody BookDTO.BookDetailDTO bookDetailDTO) {
        // 책장에 도서 등록
        String responseMessage = bookShelfService.addBookToBookshelf(bookshelfId, bookDetailDTO);

        // 성공적인 응답 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(responseMessage));
    }


    // 책장 생성
    @PostMapping
    public ResponseEntity<ApiResponse<BookShelfDTO>> createBookShelf(@RequestBody BookShelfDTO.CreateBookShelfDTO createBookShelfDTO) {
        // 책장 생성
        BookShelf createdShelf = bookShelfService.createBookShelf(createBookShelfDTO.getUserId(), createBookShelfDTO.getBookshelfName());

        // 생성된 책장의 정보를 포함한 DTO 생성
        BookShelfDTO responseDTO = new BookShelfDTO(createdShelf.getBookshelfId(), createdShelf.getBookshelfName(), null); // 책장에 포함된 책 정보를 null로 설정

        // 성공적인 응답 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }


}
