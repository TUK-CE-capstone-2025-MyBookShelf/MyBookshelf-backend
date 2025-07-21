package org.wsp.mybookshelf.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.book.entity.Book;
import org.wsp.mybookshelf.domain.user.entity.User;


@Entity
@Table(name = "BookReview")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    // 작성자 정보
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

}
