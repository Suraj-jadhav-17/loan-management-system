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
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    public void sendOtp(String email, String password) {


        mailMessage.setTo(email);
        mailMessage.setSubject("Customer Password");
        mailMessage.setText(
                "Your temporary password is: " + password +
                        "\n\nPlease use this password to log in to your account." +
                        "\n\nIf you did not request this password, please contact our support team."
        );
        javaMailSender.send(mailMessage);

    }

    public void sendEmailOtp(String email ,String otp){
        mailMessage.setTo(email);
        mailMessage.setSubject("Email Verification OTP");
        mailMessage.setText(
                "Your OTP for email verification is: " + otp +
                        "\n\nThis OTP is valid for 5 minutes."
        );

        javaMailSender.send(mailMessage);

    }
}
