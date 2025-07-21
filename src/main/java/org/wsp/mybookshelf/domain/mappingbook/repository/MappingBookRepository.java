package org.wsp.mybookshelf.domain.mappingbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.wsp.mybookshelf.domain.mappingbook.entity.MappingBook;

import java.util.List;
import java.util.Optional;

@Repository
public interface MappingBookRepository extends JpaRepository<MappingBook, String> {
    List<MappingBook> findByBookshelf_BookshelfId(Long bookshelfId);

    @Query("SELECT mb FROM MappingBook mb WHERE mb.bookshelf.bookshelfId = :bookshelfId AND mb.book.bookId = :bookId")
    Optional<MappingBook> findByBookshelfIdAndBookId(@Param("bookshelfId") Long bookshelfId, @Param("bookId") Long bookId);

    @Transactional
    void deleteByMappingId(Long mappingId);
}