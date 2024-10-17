package com.fruits.ecommerce.services.Utils;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String fromEmail;
    @Autowired
    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }
    public void sendNewPasswordEmail(String firstName, String username, String password, String email)
            throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        // Make sure to change this
        message.setFrom(fromEmail);
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
        message.setFrom(fromEmail);
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
        message.setFrom(fromEmail);
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

    public void sendPasswordResetToEmail(String firstName, String username, String password, String email)
            throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromEmail);
        message.setSubject("Password Reset Notification");
        message.setText(
                "Password Reset Notification\n" +
                        "Dear " + firstName + ",\n" +
                        "We received a request to reset your password for your account." +
                        "\nYour account details have been updated with the following information:\n" +
                        "----------------------------------------------------------\n" +
                        " ** Username: " + username + "\n" +
                        " ** Email Address: " + email + "\n" +
                        " ** Your New Password: ( " + password + " )\n" +
                        "----------------------------------------------------------\n" +
                        "\nFor your account security:\n" +
                        "1. Please change this temporary password immediately after logging in\n" +
                        "2. Never share your password with anyone\n" +
                        "3. Make sure to use a strong password\n" +
                        "\nIf you did not request this password reset, please contact our support team immediately.\n" +
                        "\nNeed help? Our support team is available 24/7.\n" +
                        "Best regards,\n" +
                        "ZAG Electronics Industries Corporation\n" +
                        "www.our-company.com\n" +
                        "Phone: +201012345678\n" +
                        "Customer Support Team"
        );

        try {
            log.info("Attempting to send password reset email to: {}", email);
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", email);
        } catch (MailException e) {
            log.error("Failed to send password reset email to: {}. Error: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendPasswordChangeConfirmationEmail(String firstName, String email) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(fromEmail);
        message.setSubject("Password Change Confirmation");
        message.setText(
                "Dear " + firstName + ",\n\n" +
                        "Your password has been changed successfully.\n" +
                        "If you did not make this change, please contact our support team immediately.\n\n" +
                        "Best regards,\n" +
                        "Our Company Support Team"
        );
        try {
            mailSender.send(message);
            log.info("Password change confirmation email sent successfully to: {}", email);
        } catch (MailException e) {
            log.error("Failed to send password change confirmation email to: {}. Error: {}", email, e.getMessage());
            throw e;
        }
    }




//--We Can Use abbreviate Format--
/**
 * "Hello " + firstName +
 * ",\n\nYour account has been created successfully.\nYour password is: "
 * + password + "\n\nThank you for joining us!"
 */

}













