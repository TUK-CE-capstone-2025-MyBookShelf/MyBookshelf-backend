package org.wsp.mybookshelf.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wsp.mybookshelf.domain.report.entity.BookReport;

import java.util.List;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {
    List<BookReport> findByBook_BookId(Long bookId);

}
