package org.wsp.mybookshelf.domain.bookshelf.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.global.commonEntity.enums.Status;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "BookShelf")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookShelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략 설정
    @Column(name = "bookshelf_id", nullable = false)
    private Long bookshelfId;

    @Column(name = "bookshelf_name", nullable = false)
    private String bookshelfName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "status")
    private String status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now(); // 생성일 자동 설정
    }
}
