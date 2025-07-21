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
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

import java.util.List;

@RestController
@RequestMapping("/api/bookshelf")
public class BookShelfController {

    @Autowired
    private BookShelfService bookShelfService;

    @Autowired
    private AladinService aladinService;

    //사용자 ID로 책장 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<BookShelfDTO>>> getBookshelvesByUser(@PathVariable Long userId) {
        List<BookShelfDTO> bookShelfList = bookShelfService.getBookShelf(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(bookShelfList));
    }

    // 책장 ID로 도서 조회 API
    @GetMapping("/book/{bookshelfId}")
    public ResponseEntity<ApiResponse<BookShelfDTO>> getBooksByBookshelf(@PathVariable Long bookshelfId) {
        BookShelfDTO bookShelfDTO = bookShelfService.getBookShelfById(bookshelfId);
        return ResponseEntity.ok(ApiResponse.onSuccess(bookShelfDTO));
    }

    //책장 카테고리 조회 테스트
    @GetMapping("/book/category/{bookshelfId}")
    public ResponseEntity<ApiResponse<List<Integer>>> getBooksCategory(@PathVariable Long bookshelfId) {
        List<Integer> categoryIds = bookShelfService.getCategoryIdsFromBookshelf(bookshelfId);
        return ResponseEntity.ok(ApiResponse.onSuccess(categoryIds));
    }


    //책장에 도서 등록
    @PostMapping("/{bookshelfId}/register")
    public ResponseEntity<ApiResponse<String>> searchAndAddBookToBookshelf(
            @PathVariable Long bookshelfId, @RequestParam String isbn13) {

        // 1. ISBN을 통해 도서 정보 검색
        BookResponse bookResponse = aladinService.searchBookDetail(isbn13);

        // 2. 검색된 도서를 BookDTO로 변환
        BookDTO bookDTO = new BookDTO(
                null,  // bookId는 새롭게 등록될 것이므로 null
                bookResponse.getTitle(),           // ✅ title
                bookResponse.getAuthor(),          // ✅ author
                bookResponse.getPublisher(),       // ✅ publisher
                bookResponse.getIsbn(),            // ✅ isbn
                bookResponse.getPublicationDate(), // ✅ publicationDate
                bookResponse.getDescription(),     // ✅ description
                bookResponse.getCover(),           // ✅ cover
                bookResponse.getCustomerReviewRank(), // ✅ customerReviewRank
                bookResponse.getSource(),          // ✅ source
                bookResponse.getCategoryId(),    // ✅ categoryID
                bookResponse.getCategoryName()     // ✅ categoryName
        );

        // 3. 책장에 도서 등록
        String responseMessage = bookShelfService.addBookToBookshelf(bookshelfId, bookDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(responseMessage));
    }


    // 책장 생성
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<BookShelfDTO>> createBookShelf(@RequestBody BookShelfDTO.CreateBookShelfDTO createBookShelfDTO) {

        System.out.println("책장생성 API 호출");
        // 책장 생성
        BookShelf createdShelf = bookShelfService.createBookShelf(createBookShelfDTO.getUserId(), createBookShelfDTO.getBookshelfName());

        // 생성된 책장의 정보를 포함한 DTO 생성
        BookShelfDTO responseDTO = new BookShelfDTO(createdShelf.getBookshelfId(), createdShelf.getBookshelfName(), null); // 책장에 포함된 책 정보를 null로 설정

        // 성공적인 응답 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }

    //책장 삭제
    @DeleteMapping("/delete/{bookshelfId}")
    public ResponseEntity<ApiResponse<String>> deleteBookShelf(@PathVariable Long bookshelfId) {
        // 책장 삭제 서비스 호출
        String responseMessage = bookShelfService.deleteBookShelf(bookshelfId);

        // 성공적인 응답 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(responseMessage));
    }

    //책장에서 도서 삭제
    @DeleteMapping("/delete/book/{bookshelfId}/{bookId}")
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable Long bookshelfId, @PathVariable Long bookId) {
        String responseMessage = bookShelfService.deleteMappingBook(bookshelfId, bookId);
        System.out.println("도서 삭제 API");
        return ResponseEntity.ok(ApiResponse.onSuccess(responseMessage));
    }

        // 책장 이름 수정
    @PatchMapping("/edit")
    public ResponseEntity<ApiResponse<BookShelfDTO>> renameBookshelf(
            @RequestBody BookShelfDTO.UpdateBookShelfDTO updateBookShelfDTO) {

        // 책장 이름 변경 서비스 호출
        BookShelfDTO updatedShelf = bookShelfService.updateBookShelfName(
                updateBookShelfDTO.getBookshelfId(),
                updateBookShelfDTO.getNewBookshelfName()
        );

        // 성공적인 응답 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(updatedShelf));
    }
}
