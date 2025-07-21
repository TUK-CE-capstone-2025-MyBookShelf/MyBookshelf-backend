package org.wsp.mybookshelf.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "BookReport")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_report_id", nullable = false)
    private Long bookReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
