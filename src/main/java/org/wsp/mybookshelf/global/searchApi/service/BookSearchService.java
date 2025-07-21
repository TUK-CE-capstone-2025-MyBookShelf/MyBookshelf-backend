//package org.wsp.mybookshelf.global.searchApi.service;
//
//import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class BookSearchService {
//    private final LibraryService libraryService;
//    private final AladinService aladinService;
//    private final GoogleBooksService googleBooksService;
//
//    public BookSearchService(LibraryService libraryService, AladinService aladinService, GoogleBooksService googleBooksService) {
//        this.libraryService = libraryService;
//        this.aladinService = aladinService;
//        this.googleBooksService = googleBooksService;
//    }
//
//
//    public Mono<List<BookResponse>> searchAllSources(String query) {
//        {
//            Mono<BookResponse> libraryMono = Mono.fromCallable(() -> libraryService.searchBooks(query))
//                    .subscribeOn(Schedulers.boundedElastic());
//            Mono<BookResponse> aladinMono = Mono.fromCallable(() -> aladinService.searchBooks(query, "Keyword", "1"))
//                    .subscribeOn(Schedulers.boundedElastic());
//            Mono<BookResponse> googleMono = googleBooksService.searchBooks(query)
//                    .subscribeOn(Schedulers.boundedElastic());
//
//            return Mono.zip(libraryMono, aladinMono, googleMono)
//                    .map(tuple -> {
//                        List<BookResponse> results = new ArrayList<>();
//                        results.add(tuple.getT1()); // 도서관 정보나루 결과
//                        results.add(tuple.getT2()); // 알라딘 API 결과
//                        results.add(tuple.getT3()); // 구글 북스 API 결과
//                        return results;
//                    });
//        }
//    }
//}
