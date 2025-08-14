package services;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.api.email.Email;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

public class EmailService {

    // Replace with your Gmail credentials (use application password for better security)
    private static final String EMAIL_USERNAME = System.getenv("EMAIL_USERNAME");  // Your Gmail address
    private static final String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");     // Your Gmail app password

    // In-memory storage for verification codes
    private static final Map<String, CodeData> verificationCodes = new HashMap<>();

    private static class CodeData {
        String code;
        long expiryTime;

        public CodeData(String code) {
            this.code = code;
            // Set expiry time to 30 minutes from now
            this.expiryTime = System.currentTimeMillis() + (30 * 60 * 1000);
        }

        public boolean isValid() {
            return System.currentTimeMillis() < expiryTime;
        }
    }

    public String sendPasswordResetCode(String recipientEmail) {
        try {
            // Generate a 6-digit verification code
            String code = generateVerificationCode();

            // Store the code in memory
            verificationCodes.put(recipientEmail, new CodeData(code));

            // Create SimpleJavaMail mailer
            Mailer mailer = MailerBuilder
                    .withSMTPServer("smtp.gmail.com", 587, EMAIL_USERNAME, EMAIL_PASSWORD)
                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                    .buildMailer();

            // Create email
            Email email = EmailBuilder.startingBlank()
                    .from("RHCONNECT", EMAIL_USERNAME)
                    .to(recipientEmail, recipientEmail)
                    .withSubject("RHCONNECT - Code de réinitialisation de mot de passe")
                    .withHTMLText(
                            "<html><body>" +
                                    "<h2>RHCONNECT - Code de Réinitialisation</h2>" +
                                    "<p>Bonjour,</p>" +
                                    "<p>Nous avons reçu une demande de réinitialisation de mot de passe pour votre compte.</p>" +
                                    "<p>Votre code de vérification est: <strong style='font-size: 24px; color: #1a73e8; " +
                                    "letter-spacing: 5px; background-color: #f0f8ff; padding: 10px; border-radius: 5px;'>" +
                                    code + "</strong></p>" +
                                    "<p>Ce code est valable pendant 30 minutes.</p>" +
                                    "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>" +
                                    "<p>Cordialement,<br>L'équipe RHCONNECT</p>" +
                                    "</body></html>"
                    )
                    .buildEmail();

            // Send the email
            mailer.sendMail(email);
            System.out.println("Reset code email sent successfully to: " + recipientEmail);

            return code;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generate a random 6-digit verification code
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(code);
    }

    /**
     * Verify if a code is valid for a given email
     */
    public boolean verifyCode(String email, String code) {
        CodeData data = verificationCodes.get(email);
        if (data == null) {
            return false; // No code exists for this email
        }

        if (!data.isValid()) {
            verificationCodes.remove(email); // Remove expired code
            return false; // Code expired
        }

        return data.code.equals(code);
    }

    /**
     * Invalidate a code after it's been used
     */
    public void invalidateCode(String email) {
        verificationCodes.remove(email);
    }
}