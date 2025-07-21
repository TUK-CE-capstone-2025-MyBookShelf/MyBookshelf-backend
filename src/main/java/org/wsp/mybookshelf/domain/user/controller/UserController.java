package org.wsp.mybookshelf.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.domain.user.dto.UserRequestDTO;
import org.wsp.mybookshelf.domain.user.dto.UserResponseDTO;
import org.wsp.mybookshelf.domain.user.entity.User;
import org.wsp.mybookshelf.domain.user.service.UserService;
import org.wsp.mybookshelf.global.response.ApiResponse;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor// 엔드포인트 수정
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRequestDTO.RegisterRequestDTO requestDTO, HttpSession session) {
        try {

            // 비밀번호 일치 여부 확인
            if (!requestDTO.getPassword().equals(requestDTO.getPasswordConfirm())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure("400", "비밀번호가 일치하지 않습니다."));
            }

            /*
            // 인증 상태 확인
            Boolean isVerified = (Boolean) session.getAttribute("isVerified");
            if (isVerified == null || !isVerified) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure("400", "이메일 인증이 필요합니다."));
            }*/

            // 사용자 등록 전에 중복 검사
            userService.isEmailDuplicate(requestDTO.getEmail());
            userService.isNickNameDuplicate(requestDTO.getNickname());

            // 사용자 등록
            User savedUser = userService.registerUser(requestDTO);

            // 응답 DTO 생성
            UserResponseDTO responseDTO = UserResponseDTO.builder()
                    .userId(savedUser.getUserId())
                    .email(savedUser.getEmail())
                    .realname(savedUser.getRealName())
                    .nickname(savedUser.getNickName())
                    .build();

            // 세션에서 인증 정보 제거
            session.removeAttribute("verificationCode");
            session.removeAttribute("userEmail");
            session.removeAttribute("isVerified");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.onSuccess(responseDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure("409", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("500", e.getMessage()));
        }
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> loginUser(
            @Valid @RequestBody UserRequestDTO.LoginRequestDTO requestDTO,
            HttpServletRequest request) {
        System.out.println("Received login request: " + requestDTO);
        try {
            UserResponseDTO userResponse = userService.loginUser(requestDTO);
            System.out.println("로그인 성공");

            // 세션에 사용자 정보 저장
            HttpSession session = request.getSession();
            session.setAttribute("userId", userResponse.getUserId()); // 세션에 userId 저장

            return ResponseEntity.ok(ApiResponse.onSuccess(userResponse));
        } catch (RuntimeException e) {
            System.out.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        System.out.println("로그인 여부 판단 호출");

        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.ok(ApiResponse.onFailure("401", "로그인되지 않았습니다."));
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findUserById(userId);

        if (user == null) {
            return ResponseEntity.ok(ApiResponse.onFailure("404", "사용자를 찾을 수 없습니다."));
        }

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .realname(user.getRealName())
                .nickname(user.getNickName())
                .build();

        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 현재 세션을 가져옴
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return ResponseEntity.ok(ApiResponse.onSuccess("로그아웃 성공"));
    }

    //이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestParam String email) {
        try {
            boolean isDuplicate = userService.isEmailDuplicate(email);
            if (isDuplicate) {
                return ResponseEntity.ok(ApiResponse.onFailure("400", "이미 사용 중인 이메일입니다."));
            }
            return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 이메일입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("500", e.getMessage()));
        }
    }


    // 닉네임 중복 검사
    @GetMapping ("/check-nickname")
    public ResponseEntity<ApiResponse<String>> checkNickName(@RequestParam String nickname) {
        try {
            boolean isDuplicate = userService.isNickNameDuplicate(nickname);
            if (isDuplicate) {
                return ResponseEntity.ok(ApiResponse.onFailure("400", "이미 사용 중인 닉네임입니다."));
            }
            return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure("500", e.getMessage()));
        }
    }


    // 비밀번호 변경
    @PutMapping ("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody UserRequestDTO.ChangePasswordRequestDTO changePasswordDTO) {
        try {
            userService.changePassword(changePasswordDTO.getEmail(), changePasswordDTO.getNewPassword());
            return ResponseEntity.ok(ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", e.getMessage()));
        }
    }
}
