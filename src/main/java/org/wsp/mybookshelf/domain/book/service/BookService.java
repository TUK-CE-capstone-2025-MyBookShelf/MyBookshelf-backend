package org.wsp.mybookshelf.domain.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.domain.book.dto.BookDTO;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.book.repository.BookRepository;

import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    
    //DB에 도서 없으면 도서 등록
    public Book addBookIfNotExists(BookDTO bookDTO) {
        Optional<Book> existingBook = bookRepository.findByIsbn(bookDTO.getIsbn());
        if (existingBook.isPresent()) {
            return existingBook.get();
        } else {
            Book newBook = Book.builder()
                    .title(bookDTO.getTitle())
                    .isbn(bookDTO.getIsbn())
                    .cover(bookDTO.getCover())
                    .author(bookDTO.getAuthor())
                    .publisher(bookDTO.getPublisher())
                    .categoryId(bookDTO.getCategoryId())
                    .categoryName(bookDTO.getCategoryName())
                    .description(bookDTO.getDescription())
                    .publicationDate(bookDTO.getPublicationDate()) // 출판일 추가
                    .customerReviewRank(bookDTO.getCustomerReviewRank()) // 고객 리뷰 순위 추가
                    .source(bookDTO.getSource()) // 데이터 출처 추가
                    .build();

            return bookRepository.save(newBook);
        }
    }


}