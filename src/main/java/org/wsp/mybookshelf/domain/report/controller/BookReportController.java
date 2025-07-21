package org.wsp.mybookshelf.domain.report.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.domain.report.dto.BookReportRequestDTO;
import org.wsp.mybookshelf.domain.report.dto.BookReportResponseDTO;
import org.wsp.mybookshelf.domain.report.service.BookReportService;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class BookReportController {

    private final BookReportService bookReportService;

    // 1. 독후감 등록
    @PostMapping
    public ResponseEntity<BookReportResponseDTO> createReport(
            @RequestBody BookReportRequestDTO dto,
            HttpSession session) {

        Object sessionUser = session.getAttribute("user");
        if (sessionUser == null || !(sessionUser instanceof User user)) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(bookReportService.createReport(dto, user));
    }

    // 2. 전체 독후감 목록 조회
    @GetMapping
    public ResponseEntity<List<BookReportResponseDTO>> getAllReports() {
        return ResponseEntity.ok(bookReportService.getAllReports());
    }

    // 3. 특정 책 독후감 목록 조회
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookReportResponseDTO>> getReportsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookReportService.getReportsByBookId(bookId));
    }
}
