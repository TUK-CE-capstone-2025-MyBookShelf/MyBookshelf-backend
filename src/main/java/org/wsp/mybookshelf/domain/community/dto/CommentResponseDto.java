package org.wsp.mybookshelf.domain.community.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private String authorName;
    private String content;
    private boolean isAnonymous;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}