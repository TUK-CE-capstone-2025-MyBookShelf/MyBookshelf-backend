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
    public Book addBookIfNotExists(BookDTO.BookDetailDTO bookDetailDTO) {
        Optional<Book> existingBook = bookRepository.findByIsbn(bookDetailDTO.getIsbn());
        if (existingBook.isPresent()) {
            return existingBook.get();
        } else {
            Book newBook = new Book();
            newBook.setTitle(bookDetailDTO.getBookTitle());
            newBook.setIsbn(bookDetailDTO.getIsbn());
            newBook.setThumbnail(bookDetailDTO.getThumbnail());
            newBook.setAuthor(bookDetailDTO.getAuthor());
            newBook.setPublisher(bookDetailDTO.getPublisher());
            newBook.setCategory(bookDetailDTO.getCategory());
            newBook.setDescription(bookDetailDTO.getDescription());
            newBook.setPage(bookDetailDTO.getPage());
            // 추가적인 필드
            return bookRepository.save(newBook);
        }
    }
}