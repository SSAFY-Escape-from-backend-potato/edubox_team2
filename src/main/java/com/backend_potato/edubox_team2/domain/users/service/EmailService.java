package com.backend_potato.edubox_team2.domain.users.service;

import jakarta.mail.MessagingException;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendVerificationLink(String email);
//    void sendVerificationCode(String email);
//    boolean verifyCode(String email, String code);
}
