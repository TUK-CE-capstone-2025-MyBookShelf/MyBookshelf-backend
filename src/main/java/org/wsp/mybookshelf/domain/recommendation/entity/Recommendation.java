package org.wsp.mybookshelf.domain.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "Recommendation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recommendation {

    @Id
    @Column(name = "recommendation_id", nullable = false)
    private String recommendationId;

    @ManyToOne // User와의 Many-To-One 관계
    @JoinColumn(name = "user_id", nullable = false) // 기본 키를 참조하므로 referencedColumnName 생략
    private User user; // User 엔티티와의 관계

    @ManyToOne // Book과의 Many-To-One 관계
    @JoinColumn(name = "book_id", nullable = false) // 기본 키를 참조하므로 referencedColumnName 생략
    private Book book; // Book 엔티티와의 관계

    @Column(name = "created_at")
    private Date createdAt;
}
