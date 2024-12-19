package com.backend_potato.edubox_team2.domain.users.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class EmailServiceImpl implements  EmailService{
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    public EmailServiceImpl(JavaMailSender mailSender, RedisTemplate<String, String> redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void sendVerificationLink(String email) {
        String verificationToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(verificationToken, email, 24, TimeUnit.HOURS);

        String link = "http://localhost:8080/api/user/verify-link?token=" + verificationToken;

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <title>Email Verification</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif; color: #333333;">
                <table align="center" width="600" cellpadding="0" cellspacing="0" border="0" style="margin: 50px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); text-align: center; font-size: 16px;">
                    <!-- Header -->
                    <tr>
                        <td style="background-color: #3cb371; color: #ffffff; padding: 20px; font-size: 24px; font-weight: bold;">
                            이메일 인증 링크
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 30px 20px;">
                            <h1 style="font-size: 22px; color: #333333; margin-bottom: 15px;">이메일 인증을 완료해주세요</h1>
                            <p style="font-size: 16px; color: #555555; margin-bottom: 20px;">
                                이메일 인증을 위해 아래 버튼을 클릭해주세요.
                            </p>
                            <a href="%s" style="display: inline-block; padding: 12px 25px; font-size: 16px; color: #ffffff; background-color: #3cb371; text-decoration: none; border-radius: 5px; font-weight: bold;">
                                이메일 인증하기
                            </a>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f9f9f9; padding: 15px; font-size: 12px; color: #777777;">
                            <p style="margin: 0;">© 2024 Your Company. All rights reserved.</p>
                            <p style="margin: 5px 0;">
                                이 메일은 자동으로 발송되었습니다. 문의사항이 있을 경우 
                                <a href="mailto:support@yourcompany.com" style="color: #3cb371; text-decoration: none;">support@yourcompany.com</a>으로 연락해주세요.
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(link);


        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Email Verification Link");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }
//    @Override
//    public void sendVerificationCode(String email) {
//        String verificationCode = String.valueOf((int) ((Math.random() * 900000) + 100000)); // Generate 6-digit code
//        redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES); // Save to Redis for 5 minutes
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Email Verification Code");
//        message.setText("Your verification code is: " + verificationCode);
//        mailSender.send(message);
//    }
//
//    @Override
//    public boolean verifyCode(String email, String code) {
//        String storedCode = redisTemplate.opsForValue().get(email);
//        return storedCode != null && storedCode.equals(code);
//    }

}
