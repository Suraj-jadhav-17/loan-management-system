package com.loanapp.loan_application.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtp(String email, String password) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Customer Password");
        mail.setText("Your temporary password is: " + password
                + "\n\nPlease use this password to log in to your account."
                + "\n\nIf you did not request this password, please contact our support team.");
        javaMailSender.send(mail);
    }

    public void sendEmailOtp(String email, String otp) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Email Verification OTP");
        mail.setText("Your OTP for email verification is: " + otp
                + "\n\nThis OTP is valid for 5 minutes.");
        javaMailSender.send(mail);
    }

    public void sendNotificationEmail(String email, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(message);
        javaMailSender.send(mail);
    }
    public void sendLoanNotification(String email, String subject, String body) {
        if (email == null || email.isBlank()) {
            log.warn("Loan notification email skipped because customer email is missing");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            log.error("Unable to send loan notification email to {}", email, ex);
        }
    }
}