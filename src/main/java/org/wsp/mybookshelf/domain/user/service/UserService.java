package org.wsp.mybookshelf.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wsp.mybookshelf.domain.user.dto.UserRequestDTO;
import org.wsp.mybookshelf.domain.user.dto.UserResponseDTO;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.entity.UserGenre;
import org.wsp.mybookshelf.domain.user.repository.UserRepository;
import org.wsp.mybookshelf.global.commonEntity.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User registerUser(UserRequestDTO.RegisterRequestDTO requestDTO) {
        // 사용자 엔티티 생성
        User newUser = User.builder()
                .email(requestDTO.getEmail())
                .realName(requestDTO.getRealname())
                .nickName(requestDTO.getNickname())
                .password(requestDTO.getPassword()) // 단순 저장 (암호화 필요)
                .birthDate(requestDTO.getBirthDate())
                .gender(requestDTO.getGender())
                .status(Status.ACTIVE) // 가입 시 상태를 ACTIVE로 설정
                .build();

        // 장르 설정
        List<UserGenre> userGenres = requestDTO.getGenre().stream()
                .map(genre -> UserGenre.builder()
                        .genre(genre) // Genre 필드 설정
                        .user(newUser) // User 설정
                        .build())
                .collect(Collectors.toList());

        newUser.setUserGenre(userGenres); // UserGenre를 User에 설정

        return userRepository.save(newUser);
    }


    @Transactional
    public UserResponseDTO loginUser(UserRequestDTO.LoginRequestDTO requestDTO) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인 (추후 암호화된 비밀번호 사용)
        if (!user.getPassword().equals(requestDTO.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 정보 응답 DTO로 변환
        return UserResponseDTO.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .realname(user.getRealName())
                .nickname(user.getNickName())
                .build();
    }

    // 이메일 중복 검사
    @Transactional(readOnly = true)
    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 검사
    @Transactional(readOnly = true)
    public boolean isNickNameDuplicate(String nickname) {
        return userRepository.existsByNickName(nickname);
    }

    // 이메일 중복 검사
    @Transactional(readOnly = true)
    public void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
    }

    // 이메일 중복 검사
    @Transactional(readOnly = true)
    public void checkNickNameDuplicate(String nickname) {
        if (userRepository.existsByNickName(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
    }

    // 사용자 검증 (이메일과 생년월일)
    public void verifyUser(String email, LocalDate birthDate) {
        User user = userRepository.findByEmailAndBirthDate(email, birthDate)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        // 추가적인 검증 로직 (예: 인증 코드 생성 등)
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(String email, String newPassword) {
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 추후 비밀번호 암호화 (필요한 경우)

        // 사용자의 비밀번호 변경
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
