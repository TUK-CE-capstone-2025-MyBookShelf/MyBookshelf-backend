package org.wsp.mybookshelf.domain.book.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wsp.mybookshelf.global.response.ApiResponse;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private AladinService aladinService;

    @GetMapping("/{isbn}")
    public ResponseEntity<ApiResponse> getBook(@PathVariable String isbn) {
        BookResponse bookResponse = aladinService.searchBookDetail(isbn);

        if (bookResponse != null) {
            return ResponseEntity.ok(ApiResponse.onSuccess(bookResponse));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("404", "책 정보를 찾을 수 없습니다."));
        }
    }
}
