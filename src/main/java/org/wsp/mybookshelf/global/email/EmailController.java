package org.wsp.mybookshelf.global.email;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wsp.mybookshelf.global.response.ApiResponse;

import java.util.Random;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    //인증번호 전송
    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponse<String>> sendVerificationEmail(
            @Valid @RequestBody EmailRequest emailRequest,
            HttpSession session) {
        try {
            // 6자리 랜덤 인증번호 생성
            int verificationCode = new Random().nextInt(900000) + 100000; // 100000 ~ 999999

            // 세션에 인증번호와 이메일 저장
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("userEmail", emailRequest.getEmail());

            System.out.println("인증번호 전송 : " + verificationCode);
            System.out.println("인증번호전송 - 세션 인증번호 : " + session.getAttribute("verificationCode"));


            // 인증 이메일 발송
            emailService.sendVerificationCode(emailRequest.getEmail(), verificationCode);

            return ResponseEntity.ok(ApiResponse.onSuccess("인증번호가 이메일로 발송되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", e.getMessage()));
        }
    }

    //인증번호 검증
    @GetMapping("/verify-code")
    public ResponseEntity<ApiResponse<String>> verifyCode(@RequestParam(required = false) Integer inputCode, HttpSession session) {
        // 입력값이 없는 경우
        if (inputCode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", "인증번호를 입력하지 않았습니다."));
        }
        System.out.println("입력 인증번호: " + inputCode);

        // 세션에서 인증번호 가져오기
        Integer sessionCode = (Integer) session.getAttribute("verificationCode");

        // 세션 상태 로깅
        System.out.println("세션 상태: " + session); // 세션 정보 출력
        System.out.println("저장된 인증번호: " + sessionCode); // 인증번호 출력


        // 세션이 초기화된 경우
        if (sessionCode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", "인증번호가 세션에 저장되어 있지 않습니다. 다시 인증번호를 요청하세요."));
        }

        // 인증번호 검증
        if (sessionCode.equals(inputCode)) {
            // 인증번호가 유효할 경우
            session.setAttribute("isVerified", true); // 인증 완료 플래그 설정
            return ResponseEntity.ok(ApiResponse.onSuccess("인증번호가 확인되었습니다. 회원가입을 진행하세요."));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure("400", "유효하지 않은 인증 코드입니다."));
    }
}