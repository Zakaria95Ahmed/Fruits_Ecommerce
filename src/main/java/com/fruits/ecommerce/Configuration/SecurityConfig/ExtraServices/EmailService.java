package com.fruits.ecommerce.Configuration.SecurityConfig.ExtraServices;

import jakarta.mail.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNewPasswordEmail(String firstName, String username, String password, String email)
            throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        // Make sure to change this
        message.setFrom("zakariayahmed@gmail.com");
        message.setSubject("Welcome to Our Service");
        message.setText(
                "Welcome to Our Company Ltd. Platform!\n" +
                        "Dear " + firstName + ",\n" +
                        "We're thrilled to have you join our platform!.\n Your registration has been successfully completed, and your account is now active." +
                        "\nPlease find your account details below:\n" +
                        "----------------------------------------------------------\n" +
                        " ** Username: " + username + "\n" +
                        " ** Email Address: " + email + "\n" +
                        " ** Your Password: ( " + password + " )\n" +
                        "----------------------------------------------------------\n" +
                        "For security reasons, we strongly recommend changing your password upon your first login.\n" +
                        "Should you have any questions or require assistance, our support team is here to help. Don't hesitate to reach out to us.\n" +
                        "Thank you for choosing our services. We look forward to providing you with an exceptional experience!\n" +
                        "Best regards,\n" +
                        "ZAG Electronics Industries Corporation\n" +
                        "www.our-company.com\n" +
                        "Phone: +201012345678\n" +
                        "Customer Support Team"
        );
        try {
            log.info("Attempting to send email to: {}", email);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", email);
        } catch (MailException e) {
            log.error("Failed to send email to: {}. Error: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendAccountLockedEmail(String firstName, String email) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("zakariayahmed@gmail.com");
        message.setSubject("Account Locked Notification");
        message.setText(
                "Dear " + firstName + ",\n\n" +
                        "Your account has been locked due to multiple failed login attempts.\n" +
                        "Please contact our support team for assistance.\n\n" +
                        "Best regards,\n" +
                        "Our Company Support Team"
        );
        try {
            mailSender.send(message);
            log.info("Account locked email sent successfully to: {}", email);
        } catch (MailException e) {
            log.error("Failed to send account locked email to: {}. Error: {}", email, e.getMessage());
            throw e;
        }
    }

    public void sendAccountUnlockedEmail(String firstName, String email) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("zakariayahmed@gmail.com");
        message.setSubject("Account Unlocked Notification");
        message.setText(
                "Dear " + firstName + ",\n\n" +
                        "Good news! Your account has been unlocked, and you can now log in.\n" +
                        "If you have any questions, please contact our support team.\n\n" +
                        "Best regards,\n" +
                        "Our Company Support Team"
        );
        try {
            mailSender.send(message);
            log.info("Account unlocked email sent successfully to: {}", email);
        } catch (MailException e) {
            log.error("Failed to send account unlocked email to: {}. Error: {}", email, e.getMessage());
            throw e;
        }
    }


}


//-- abbreviate Format--

/**
 * "Hello " + firstName +
 * ",\n\nYour account has been created successfully.\nYour password is: "
 * + password + "\n\nThank you for joining us!"
 */










