package org.wsp.mybookshelf.domain.mappingbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wsp.mybookshelf.domain.mappingbook.entity.MappingBook;

import java.util.List;

@Repository
public interface MappingBookRepository extends JpaRepository<MappingBook, String> {
    List<MappingBook> findByBookshelf_BookshelfId(Long bookshelfId);
}