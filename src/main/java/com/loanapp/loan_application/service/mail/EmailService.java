package com.loanapp.loan_application.service.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtp(String email, String password) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Customer Password");
        mailMessage.setText(
                "Your temporary password is: " + password +
                        "\n\nPlease use this password to log in to your account." +
                        "\n\nIf you did not request this password, please contact our support team."
        );
        javaMailSender.send(mailMessage);

    }
}
