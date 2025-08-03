package org.wsp.mybookshelf.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 게시글의 댓글인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 누가 작성했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    // 댓글 내용
    @Lob
    @Column(nullable = false)
    private String content;

    // 익명 여부
    private boolean isAnonymous;

    // 시간 기록
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Comment(Post post, User author, String content, boolean isAnonymous) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 댓글 수정 로직 포함
    public void update(String content, boolean isAnonymous) {
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.updatedAt = LocalDateTime.now();
    }
}