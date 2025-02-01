package org.wsp.mybookshelf.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.wsp.mybookshelf.global.commonEntity.enums.Gender;
import org.wsp.mybookshelf.global.commonEntity.enums.Genre;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
public class UserRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequestDTO {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    //비밀번호 변경용 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChangePasswordRequestDTO {
        @Email
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @NotBlank(message = "생년월일은 필수입니다.")
        private String birthDate; // 생년월일 (String으로 받을 경우 처리 필요)

        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String newPassword; // 새로운 비밀번호
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequestDTO {

        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @NotBlank(message = "닉네임은 필수입니다.")
        private String nickname;

        @NotBlank(message = "실명은 필수입니다.")
        private String realname;

        @NotBlank
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") // 비밀번호 길이 체크
        private String password;

        @NotBlank(message = "비밀번호를 다시 한번 입력해주세요")
        private String passwordConfirm;

        @NotNull(message = "생년월일은 필수입니다.")
        private LocalDate birthDate;

        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;


        @NotNull(message = "선호 장르는 필수입니다.")
        private List<Genre> genre; // 여러 장르를 받을 수 있도록 리스트로 변경

    }
}
