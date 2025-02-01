package org.wsp.mybookshelf.domain.bookshelf.service;

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
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;

import java.util.List;
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



    //책장 조회
    @Transactional
    public List<BookShelfDTO> getBookShelf(Long userId){
        // 사용자 ID에 따라 책장을 조회
        List<BookShelf> bookshelfList = bookShelfRepository.findByUser_UserId(userId);

        // 책장 정보를 DTO로 변환
        return bookshelfList.stream().map(bookshelf -> {
            // 각 책장에 대한 매핑된 책들을 조회
            List<MappingBook> mappings = mappingBookRepository.findByBookshelf_BookshelfId(bookshelf.getBookshelfId());
            List<BookDTO> books = mappings.stream().map(mapping -> {
                Book book = mapping.getBook();
                return new BookDTO(book.getBookId(), book.getTitle(), book.getIsbn(), book.getThumbnail());
            }).collect(Collectors.toList());

            return new BookShelfDTO(bookshelf.getBookshelfId(), bookshelf.getBookshelfName(), books);
        }).collect(Collectors.toList());
    }

    //책장에 도서 등록
    @Transactional
    public String addBookToBookshelf(Long bookshelfId, BookDTO.BookDetailDTO bookDetailDTO) {
        // 책장 찾기
        BookShelf bookshelf = bookShelfRepository.findById(bookshelfId).orElseThrow(() ->
                new RuntimeException("책장을 찾을 수 없습니다."));

        // 책 추가 또는 존재 여부 확인
        Book book = bookService.addBookIfNotExists(bookDetailDTO);

        // 매핑 추가
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

}
