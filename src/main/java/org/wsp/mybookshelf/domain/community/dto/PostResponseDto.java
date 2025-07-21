package org.wsp.mybookshelf.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.wsp.mybookshelf.domain.community.entity.BoardType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private boolean isAnonymous;
    private BoardType boardType;
    private LocalDateTime createdAt;

    private Long likeCount;
    private Boolean likedByUser; // 좋아요 눌렀는지 확인
}
