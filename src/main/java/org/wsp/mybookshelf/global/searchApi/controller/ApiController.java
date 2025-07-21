package org.wsp.mybookshelf.global.searchApi.controller;

import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;
import org.wsp.mybookshelf.global.searchApi.service.GoogleBooksService;
import org.wsp.mybookshelf.global.searchApi.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class ApiController {
    private final LibraryService libraryService;
    private final AladinService aladinService;
    private final GoogleBooksService googleBooksService;

    public ApiController(LibraryService libraryService, AladinService aladinService, GoogleBooksService googleBooksService) {
        this.libraryService = libraryService;
        this.aladinService = aladinService;
        this.googleBooksService = googleBooksService;
    }

    // 알라딘 OpenAPI가 아닌 LibraryAPI 사용 (406 오류 해결)
    @GetMapping("/searchLibrary")
    public Mono<ResponseEntity<Map<String, List<BookResponse>>>> searchBooks(
            @RequestParam String query
    ) {
        return Mono.fromCallable(() -> {
            List<BookResponse> books = libraryService.searchBooks(query); // ✅ static 제거
            return ResponseEntity.ok(Map.of("books", books)); // ✅ ResponseEntity 사용 (JSON 명확화)
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 알라딘 OpenAPI만 사용한 코드
    @GetMapping("/search")
    public Mono<Map<@org.jetbrains.annotations.NotNull String, @org.jetbrains.annotations.NotNull List<BookResponse>>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "Keyword") String queryType,
            @RequestParam(defaultValue = "1") String start,
            @RequestParam(defaultValue = "100") int maxResults,
            @RequestParam(defaultValue = "true") boolean showDetail
    ) {
        return Mono.fromCallable(() -> {
            List<BookResponse> books = aladinService.searchBooks(query, queryType, start, maxResults, showDetail);
            //List<BookResponse> books = LibraryService.searchBooks(query);
            return Map.of("books", books);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // 알라딘 + GoogleAPI 결합 검색 (비동기 방식)
    @GetMapping("/search/all")
    public Mono<ResponseEntity<Map<String, Object>>> searchAllBooks(@RequestParam String query) {
        Mono<List<BookResponse>> libraryBooks = Mono.fromCallable(() -> libraryService.searchBooks(query))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<List<BookResponse>> googleBooks = googleBooksService.searchBooks(query)
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(libraryBooks, googleBooks)
                .map(tuple -> ResponseEntity.ok(Map.of(
                        "libraryAPI", tuple.getT1(),
                        "googleBooksAPI", tuple.getT2()
                )));
    }

    // 검색하여 상세 정보 리턴
    @GetMapping("/search/register")
    public BookResponse searchBookDetail(@RequestParam String isbn13) {
        BookResponse book = aladinService.searchBookDetail(isbn13);

        return book;
    }
}