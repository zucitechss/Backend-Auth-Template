package com.auth.template.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSignupSuccessEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Signup Successful");
        message.setText("Hello " + username + ",\n\nYour signup is successful!");
        try {
            mailSender.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendOtpForPasswordReset(String toEmail, String otp) {
        String subject = "Password Reset OTP";
        String text = "Your OTP for password reset is: " + otp + "\nThis OTP is valid for 10 minutes.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}