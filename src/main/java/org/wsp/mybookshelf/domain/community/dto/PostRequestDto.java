package org.wsp.mybookshelf.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.wsp.mybookshelf.domain.community.entity.BoardType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private boolean isAnonymous;
    private BoardType boardType;
}
