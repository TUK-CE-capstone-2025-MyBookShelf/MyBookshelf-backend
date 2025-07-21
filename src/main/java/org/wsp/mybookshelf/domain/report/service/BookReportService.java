package org.wsp.mybookshelf.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.book.repository.BookRepository;
import org.wsp.mybookshelf.domain.report.dto.BookReportRequestDTO;
import org.wsp.mybookshelf.domain.report.dto.BookReportResponseDTO;
import org.wsp.mybookshelf.domain.report.entity.BookReport;
import org.wsp.mybookshelf.domain.report.repository.BookReportRepository;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReportService {

    private final BookReportRepository bookReportRepository;
    private final BookRepository bookRepository;

    // 독후감 등록
    public BookReportResponseDTO createReport(BookReportRequestDTO dto, User user) {
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("해당 책이 존재하지 않습니다."));

        BookReport report = BookReport.builder()
                .book(book)
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        bookReportRepository.save(report);

        return BookReportResponseDTO.builder()
                .id(report.getBookReportId())
                .title(report.getTitle())
                .content(report.getContent())
                .bookTitle(book.getTitle())
                .userNickname(user.getNickName())
                .createdAt(LocalDate.now().toString())
                .build();
    }

    // 전체 독후감 조회
    public List<BookReportResponseDTO> getAllReports() {
        return bookReportRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 책에 대한 독후감 조회
    public List<BookReportResponseDTO> getReportsByBookId(Long bookId) {
        return bookReportRepository.findByBook_BookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 내부 변환 메서드
    private BookReportResponseDTO convertToDTO(BookReport report) {
        return BookReportResponseDTO.builder()
                .id(report.getBookReportId())
                .title(report.getTitle())
                .content(report.getContent())
                .bookTitle(report.getBook().getTitle())
                .userNickname(report.getUser().getNickName())
                .createdAt(report.getCreatedAt().toString())
                .build();
    }

}
