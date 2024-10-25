package com.fruits.ecommerce.services.Utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(String.format("%s <%s>", "Our Company Support", fromEmail));
            helper.setSubject(subject);
            helper.setText(wrapHtmlContent(htmlContent), true);

            log.info("Attempting to send email to: {}", to);
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", to);

            // Add a simple delay between emails
            Thread.sleep(1000);

        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }


    // A unified HTML template for all emails
    private String wrapHtmlContent(String content) {
        return String.format("""
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body style='
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    '>
                        <div style='
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            padding: 20px;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        '>
                            <div style='text-align: center; margin-bottom: 20px;'>
                                <h1 style='color: #2c3e50; margin: 0;'>Our Company</h1>
                            </div>
                            %s
                            <div style='
                                margin-top: 30px;
                                padding-top: 20px;
                                border-top: 1px solid #eee;
                                font-size: 12px;
                                color: #666;
                                text-align: center;
                            '>
                                <p>This is an automated message, please do not reply.</p>
                                <p>Our Company Ltd. • Phone: +201012345678 • www.our-company.com</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """, content);
    }

    public void sendNewPasswordEmail(String firstName, String username, String password, String email)
            throws MessagingException {
        String htmlContent = String.format("""
                    <div style='text-align: left;'>
                        <h2 style='color: #2c3e50;'>Welcome %s!</h2>
                        <p>We're thrilled to have you join our platform! Your registration has been successfully completed.</p>
                        
                        <div style='
                            background-color: #f8f9fa;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 5px;
                            border-left: 4px solid #2c3e50;
                        '>
                            <h3 style='margin-top: 0; color: #2c3e50;'>Your Account Details</h3>
                            <p style='margin: 10px 0;'><strong>Username:</strong> %s</p>
                            <p style='margin: 10px 0;'><strong>Email:</strong> %s</p>
                            <p style='margin: 10px 0;'><strong>Password:</strong> %s</p>
                        </div>
                        
                        <div style='margin-top: 20px;'>
                            <p><strong>Important Security Note:</strong></p>
                            <ul style='padding-left: 20px;'>
                                <li>Please change your password upon first login</li>
                                <li>Keep your credentials secure</li>
                                <li>Never share your password with anyone</li>
                            </ul>
                        </div>
                        
                        <p style='margin-top: 20px;'>Need help? Our support team is here for you.</p>
                        
                        <p style='margin-top: 20px;'>
                            Best regards,<br>
                            Support Team
                        </p>
                    </div>
                """, firstName, username, email, password);

        sendHtmlEmail(email, "Welcome to Our Platform! 🎉", htmlContent);
    }

    public void sendAccountLockedEmail(String firstName, String email) throws MessagingException {
        String htmlContent = String.format("""
                    <div style='text-align: left;'>
                        <h2 style='color: #e74c3c;'>Account Security Alert</h2>
                        <p>Dear %s,</p>
                        
                        <div style='
                            background-color: #fdf1f0;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 5px;
                            border-left: 4px solid #e74c3c;
                        '>
                            <p style='margin: 0;'><strong>Your account has been temporarily locked</strong> due to multiple failed login attempts.</p>
                        </div>
                        
                        <p>For your security, please contact our support team to unlock your account.</p>
                        
                        <p style='margin-top: 20px;'>
                            Best regards,<br>
                            Security Team
                        </p>
                    </div>
                """, firstName);

        sendHtmlEmail(email, "Account Security Alert 🔒", htmlContent);
    }

    public void sendAccountUnlockedEmail(String firstName, String email) throws MessagingException {
        String htmlContent = String.format("""
                    <div style='text-align: left;'>
                        <h2 style='color: #27ae60;'>Account Unlocked Successfully</h2>
                        <p>Dear %s,</p>
                        
                        <div style='
                            background-color: #f0faf0;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 5px;
                            border-left: 4px solid #27ae60;
                        '>
                            <p style='margin: 0;'>Your account has been successfully unlocked. You can now log in.</p>
                        </div>
                        
                        <p>If you have any questions, our support team is here to help.</p>
                        
                        <p style='margin-top: 20px;'>
                            Best regards,<br>
                            Support Team
                        </p>
                    </div>
                """, firstName);

        sendHtmlEmail(email, "Your Account is Now Unlocked 🔓", htmlContent);
    }

    public void sendPasswordResetEmail(String firstName, String username, String password, String email)
            throws MessagingException {
        String htmlContent = String.format("""
                    <div style='text-align: left;'>
                        <h2 style='color: #2c3e50;'>Password Reset Confirmation</h2>
                        <p>Dear %s,</p>
                        
                        <div style='
                            background-color: #f8f9fa;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 5px;
                            border-left: 4px solid #2c3e50;
                        '>
                            <h3 style='margin-top: 0; color: #2c3e50;'>Your New Account Details</h3>
                            <p style='margin: 10px 0;'><strong>Username:</strong> %s</p>
                            <p style='margin: 10px 0;'><strong>Email:</strong> %s</p>
                            <p style='margin: 10px 0;'><strong>New Password:</strong> %s</p>
                        </div>
                        
                        <div style='
                            background-color: #fff3cd;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 5px;
                            border-left: 4px solid #ffc107;
                        '>
                            <p style='margin: 0;'><strong>Security Reminder:</strong></p>
                            <ul style='margin: 10px 0; padding-left: 20px;'>
                                <li>Change this temporary password immediately after logging in</li>
                                <li>Use a strong password with mixed characters</li>
                                <li>Never share your password with anyone</li>
                            </ul>
                        </div>
                        
                        <p>If you didn't request this password reset, please contact our support team immediately.</p>
                        
                        <p style='margin-top: 20px;'>
                            Best regards,<br>
                            Security Team
                        </p>
                    </div>
                """, firstName, username, email, password);

        sendHtmlEmail(email, "Password Reset Confirmation 🔑", htmlContent);
    }

    public void sendPasswordChangeConfirmationEmail(String firstName, String email) throws MessagingException {
        String htmlContent = String.format("""
                    <div style='text-align: left;'>
                        <h2 style='color: #27ae60;'>Password Changed Successfully</h2>
                        <p>Dear %s,</p>
                        
                        <div style='
                            background-color: #f0faf0;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 5px;
                            border-left: 4px solid #27ae60;
                        '>
                            <p style='margin: 0;'>Your password has been successfully changed.</p>
                        </div>
                        
                        <div style='margin-top: 20px;'>
                            <p>If you did not make this change, please contact our support team immediately.</p>
                        </div>
                        
                        <p style='margin-top: 20px;'>
                            Best regards,<br>
                            Security Team
                        </p>
                    </div>
                """, firstName);

        sendHtmlEmail(email, "Password Change Confirmation ✔️", htmlContent);
    }

}


//--We Can Use abbreviate Format--
/**
 * "Hello " + firstName +
 * ",\n\nYour account has been created successfully.\nYour password is: "
 * + password + "\n\nThank you for joining us!"
 */















