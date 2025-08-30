package com.scriptopia.demo.service;

import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.LocalAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final LocalAccountRepository localAccountRepository;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${reset-url}")
    private String resetUrl;

    public void sendVerificationCode(String toEmail, String code) {
        mailSender.send(initMessage(
                toEmail,
                fromEmail,
                "[Scriptopia] 회원가입 이메일 인증번호",
                "인증번호: " + code + "\n5분 이내에 입력해주세요."

        ));
    }

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(
                "email:verify:" + email,
                code,
                5,
                TimeUnit.MINUTES
        );
    }

    public void sendResetLink(String toEmail, String token) {

        String link = resetUrl + "?token=" + token;
        mailSender.send(initMessage(
                toEmail,
                fromEmail,
                "[Scriptopia] 비밀번호 재설정 안내",
                "아래 링크를 클릭하여 비밀번호를 변경하세요:\n" +link
        ));
    }


    public SimpleMailMessage initMessage(String toEmail, String fromEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }
    public String getCode(String email) {
        return redisTemplate.opsForValue().get("email:verify" + email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete("email:verify:" + email);
    }



}
