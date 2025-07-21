package org.wsp.mybookshelf.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.wsp.mybookshelf.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    private String title;

    @Lob
    private String content;

    private boolean isAnonymous;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 수정 메서드
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
