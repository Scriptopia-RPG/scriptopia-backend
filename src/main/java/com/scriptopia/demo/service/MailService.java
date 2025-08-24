package com.scriptopia.demo.service;

import lombok.RequiredArgsConstructor;
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

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("회원가입 이메일 인증번호");
        message.setText("인증번호: " + code + "\n5분 이내에 입력해주세요.");
        mailSender.send(message);

    }

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(
                "email:verify:" + email,
                code,
                5,
                TimeUnit.MINUTES
        );
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get("email:verify" + email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete("email:verify:" + email);
    }



}
