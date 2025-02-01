package org.wsp.mybookshelf.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;             // 사용자 ID
    private String email;        // 이메일
    private String realname;         // 본명
    private String nickname;     // 닉네임
}