package org.wsp.mybookshelf.global.email;

public interface EmailService {
    void sendVerificationCode(String email, int code); // 새로운 메소드
}