package org.wsp.mybookshelf.global.email;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationCode(String to, int code) {
        String subject = "이메일 인증 코드";
        String message = "아래 인증 코드를 입력하세요: " + code;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("your_email@example.com"); // 발신자 이메일

        mailSender.send(mailMessage);
    }
}
