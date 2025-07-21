package org.wsp.mybookshelf.domain.bookshelf.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wsp.mybookshelf.domain.book.dto.BookDTO;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.book.repository.BookRepository;
import org.wsp.mybookshelf.domain.book.service.BookService;
import org.wsp.mybookshelf.domain.bookshelf.dto.BookShelfDTO;
import org.wsp.mybookshelf.domain.bookshelf.entity.BookShelf;
import org.wsp.mybookshelf.domain.bookshelf.repository.BookShelfRepository;
import org.wsp.mybookshelf.domain.mappingbook.entity.MappingBook;
import org.wsp.mybookshelf.domain.mappingbook.repository.MappingBookRepository;
import org.wsp.mybookshelf.domain.recommendation.service.LoanBookService;
import org.wsp.mybookshelf.domain.recommendation.service.RecommendCategoryMappingService;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;
import org.wsp.mybookshelf.domain.user.service.UserService;
import org.wsp.mybookshelf.global.searchApi.dto.BookResponse;
import org.wsp.mybookshelf.global.searchApi.service.AladinService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookShelfService {

    @Autowired
    private BookShelfRepository bookShelfRepository;

    @Autowired
    private MappingBookRepository mappingBookRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private AladinService aladinService;

    @Autowired
    private LoanBookService loanBookService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecommendCategoryMappingService categoryMappingService;

    //책장 조회
    @Transactional
    public List<BookShelfDTO> getBookShelf(Long userId) {
        // 사용자 ID에 따라 책장을 조회
        List<BookShelf> bookshelfList = bookShelfRepository.findByUser_UserId(userId);

        // 책장 정보를 DTO로 변환
        return bookshelfList.stream().map(bookshelf -> {
            // 각 책장에 대한 매핑된 책들을 조회
            List<MappingBook> mappings = mappingBookRepository.findByBookshelf_BookshelfId(bookshelf.getBookshelfId());
            List<BookDTO> books = mappings.stream().map(mapping -> {
                Book book = mapping.getBook();
                return new BookDTO(
                        book.getBookId(),          // ✅ bookId
                        book.getTitle(),           // ✅ title
                        book.getAuthor(),          // ✅ author
                        book.getPublisher(),       // ✅ publisher
                        book.getIsbn(),            // ✅ isbn
                        book.getPublicationDate(), // ✅ publicationDate
                        book.getDescription(),     // ✅ description
                        book.getCover(),           // ✅ cover
                        book.getCustomerReviewRank(), // ✅ customerReviewRank
                        book.getSource(),          // ✅ source
                        book.getCategoryId(),    // ✅ categoryID
                        book.getCategoryName()     // ✅ categoryName
                );


            }).collect(Collectors.toList());

            return new BookShelfDTO(bookshelf.getBookshelfId(), bookshelf.getBookshelfName(), books);
        }).collect(Collectors.toList());
    }

    //책장 ID로 책장의 도서 조회
    @Transactional
    public BookShelfDTO getBookShelfById(Long bookshelfId) {
        // 책장 ID로 책장 조회 (없으면 예외 발생)
        BookShelf bookshelf = bookShelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new RuntimeException("해당 책장을 찾을 수 없습니다."));

        // 해당 책장에 매핑된 도서 목록 조회
        List<MappingBook> mappings = mappingBookRepository.findByBookshelf_BookshelfId(bookshelfId);

        // BookDTO 리스트 변환
        List<BookDTO> books = mappings.stream().map(mapping -> {
            Book book = mapping.getBook();
            return new BookDTO(
                    book.getBookId(),          // ✅ bookId
                    book.getTitle(),           // ✅ title
                    book.getAuthor(),          // ✅ author
                    book.getPublisher(),       // ✅ publisher
                    book.getIsbn(),            // ✅ isbn
                    book.getPublicationDate(), // ✅ publicationDate
                    book.getDescription(),     // ✅ description
                    book.getCover(),           // ✅ cover
                    book.getCustomerReviewRank(), // ✅ customerReviewRank
                    book.getSource(),          // ✅ source
                    book.getCategoryId(),    // ✅ categoryID
                    book.getCategoryName()     // ✅ categoryName
            );
        }).collect(Collectors.toList());

        // BookShelfDTO 생성 및 반환
        return new BookShelfDTO(bookshelf.getBookshelfId(), bookshelf.getBookshelfName(), books);
    }



    //책장에 도서 등록
    @Transactional
    public String addBookToBookshelf(Long bookshelfId, BookDTO bookDTO) {
        // 책장 찾기
        BookShelf bookshelf = bookShelfRepository.findById(bookshelfId).orElseThrow(() ->
                new RuntimeException("책장을 찾을 수 없습니다."));

        //존재 여부 확인 후 도서 등록 로직
        Book book = bookService.addBookIfNotExists(bookDTO);

        // 매핑 (도서-책장) 추가
        MappingBook mappingBook = MappingBook.builder()
                .bookshelf(bookshelf)
                .book(book)
                .build();

        mappingBookRepository.save(mappingBook);

        return "책이 성공적으로 추가되었습니다.";
    }

    public BookShelf createBookShelf(Long userId, String bookshelfName) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("사용자를 찾을 수 없습니다."));

        BookShelf bookShelf = new BookShelf();
        bookShelf.setBookshelfName(bookshelfName);
        bookShelf.setUser(user); // 사용자 설정
        return bookShelfRepository.save(bookShelf);
    }


    //책장 삭제
    @Transactional
    public String deleteBookShelf(Long bookshelfId) {

        // 책장 삭제
        bookShelfRepository.deleteById(bookshelfId);
        return "책장이 성공적으로 삭제되었습니다.";
    }

    @Transactional
    public String deleteMappingBook(Long bookshelfId, Long bookId) {
        // 1. bookshelfId와 bookId로 MappingBook 찾기
        MappingBook mappingBook = mappingBookRepository.findByBookshelfIdAndBookId(bookshelfId, bookId)
                .orElseThrow(() -> new EntityNotFoundException("해당 도서가 책장에 없습니다."));

        // 2. 찾은 mappingBook의 mappingId로 삭제
        System.out.println("삭제할 매핑" + mappingBook);
        mappingBookRepository.deleteByMappingId(mappingBook.getMappingId());

        return "삭제 완료";
    }
    
    //책장 이름 수정
    @Transactional
    public BookShelfDTO updateBookShelfName(Long bookshelfId, String newName) {
        // 책장이 존재하는지 확인
        BookShelf bookShelf = bookShelfRepository.findById(bookshelfId)
                .orElseThrow(() -> new RuntimeException("책장이 존재하지 않습니다."));

        // 책장 이름 변경
        bookShelf.setBookshelfName(newName);

        // 변경된 책장 저장
        BookShelf updatedShelf = bookShelfRepository.save(bookShelf);

        // 변경된 책장 정보를 DTO로 변환하여 반환
        return new BookShelfDTO(updatedShelf.getBookshelfId(), updatedShelf.getBookshelfName(), null);
    }

    @Transactional(readOnly = true)
    public List<String> getIsbn(Long bookshelfId) {
        // 책장 ID에 해당하는 도서의 ISBN 목록 조회
        return mappingBookRepository.findByBookshelf_BookshelfId(bookshelfId).stream()
                .map(mappingBook -> mappingBook.getBook().getIsbn()) // 도서의 ISBN 추출
                .collect(Collectors.toList());
    }

    //책장의 책 장르 정보 수집
    @Transactional
    public List<Integer> getCategoryIdsFromBookshelf(Long bookshelfId) {
        // 책장 정보 조회
        BookShelfDTO bookShelfDTO = getBookShelfById(bookshelfId);

        // 책장에 있는 책들의 카테고리 ID 목록 추출
        return bookShelfDTO.getBook().stream()
                .map(BookDTO::getCategoryId)
                .collect(Collectors.toList());
    }

    //카테고리로 추천 받아오기
    @Transactional
    public List<BookResponse> getRecommendedBooksByCategory(Long bookshelfId) {
        // 책장 내 도서 카테고리 ID 개수 집계
        List<Integer> categoryIds = getCategoryIdsFromBookshelf(bookshelfId);

        // 중복 카테고리 개수 집계
        Map<Integer, Long> categoryCountMap = categoryIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println("[책장 ID]: " + bookshelfId);
        System.out.println("[카테고리별 도서 개수]: " + categoryCountMap);

        List<BookResponse> recommendedBooks = new ArrayList<>();
        int defaultBooksPerCategory = 10; // 기본 10권

        for (Map.Entry<Integer, Long> entry : categoryCountMap.entrySet()) {
            Integer categoryId = entry.getKey();
            Long count = entry.getValue();

            // 해당 카테고리에서 가져올 책 개수 설정
            int booksToFetch = defaultBooksPerCategory * count.intValue(); // 중복된 횟수만큼 곱하기

            System.out.println("[카테고리 ID: " + categoryId + "] 가져올 책 개수: " + booksToFetch);

            // 알라딘 API 호출
            List<BookResponse> books = aladinService.searchBooksByCategory(categoryId, booksToFetch);
            System.out.println("[카테고리 ID: " + categoryId + "] 검색된 도서 개수: " + books.size());
            recommendedBooks.addAll(books);
        }

        System.out.println("[최종 추천 도서 개수]: " + recommendedBooks.size());

        return recommendedBooks;
    }

    public Long getUserId(Long bookshelfId) {
        Optional<BookShelf> bookshelf = bookShelfRepository.findById(bookshelfId);
        return (Long) bookshelf.map(b -> b.getUser().getId()).orElse(null);
    }


    public List<BookResponse> getRecommendedBooksByBookshelfId(Long bookshelfId) {
        BookShelfDTO bookShelf = getBookShelfById(bookshelfId);

        Long userId = getUserId(bookshelfId);
        User user = userService.findUserById(userId);
        int userAge = userService.getUserAge(String.valueOf(user.getBirthDate()));

        Map<String, Integer> kdcCountMap = new HashMap<>();
        for (BookDTO book : bookShelf.getBook()) {
            if (book.getCategoryId() != null) {
                String kdc = categoryMappingService.getKdcByCategoryId(book.getCategoryId());
                if (kdc != null && kdc.matches("\\d+")) {
                    kdcCountMap.put(kdc, kdcCountMap.getOrDefault(kdc, 0) + 1);
                }
            }
        }
        System.out.println("책장 이름 : " + bookShelf.getBookshelfName());
        System.out.println("[책장 기반 추천] KDC 코드별 개수: " + kdcCountMap);

        List<BookResponse> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : kdcCountMap.entrySet()) {
            String kdc = entry.getKey();
            int bookCount = entry.getValue() * 10;

            List<BookResponse> books = loanBookService.getPopularBooksByKdc(kdc, userAge, bookCount);
            result.addAll(books);
        }

        return result;
    }


}
