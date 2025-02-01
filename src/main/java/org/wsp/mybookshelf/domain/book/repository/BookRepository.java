package org.wsp.mybookshelf.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wsp.mybookshelf.domain.book.entity.Book;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title); // 제목으로 책 조회
    Optional<Book> findByIsbn(String isbn);   // ISBN으로 책 조회
}
