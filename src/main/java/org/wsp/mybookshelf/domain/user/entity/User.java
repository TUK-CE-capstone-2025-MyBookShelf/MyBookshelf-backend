package org.wsp.mybookshelf.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.wsp.mybookshelf.global.commonEntity.enums.Gender;
import org.wsp.mybookshelf.global.commonEntity.enums.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Column(name = "password", nullable = false)
    private String password;

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "realname")
    private String realName;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Column(name = "nickname", unique = true, nullable = false)
    private String nickName;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGenre> userGenre = new ArrayList<>(); // 여러 장르를 관리

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status; // 사용자 상태

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now(); // 생성일 자동 설정
        this.status = Status.ACTIVE; // 기본 상태를 활성으로 설정
    }
}

