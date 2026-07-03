package com.example.authenticationsystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("janeebaimay@gmail.com");   // <-- Add this line
        message.setTo(to);
        message.setSubject("Authentication System OTP");
        message.setText(
                "Your One-Time Password is: "
                        + otp +
                        "\n\nThis code expires in 5 minutes."
        );

        mailSender.send(message);
    }
}
