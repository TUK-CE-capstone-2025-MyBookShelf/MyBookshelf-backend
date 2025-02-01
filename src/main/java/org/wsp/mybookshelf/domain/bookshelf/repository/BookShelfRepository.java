package org.wsp.mybookshelf.domain.bookshelf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wsp.mybookshelf.domain.bookshelf.entity.BookShelf;

import java.util.List;

@Repository
public interface BookShelfRepository extends JpaRepository<BookShelf, Long> {
    List<BookShelf> findByUser_UserId(Long userId); //userId로 책장 목록 조회


}