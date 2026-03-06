package com.travelmateai.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Async helper for sending emails.
 * Separated from EmailService so that Spring AOP proxies correctly
 * intercept @Async calls (self-invocation within the same class bypasses
 * proxies).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAsyncHelper {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send booking confirmation email asynchronously.
     */
    @Async("taskExecutor")
    public void sendConfirmationAsync(String userName, String userEmail, Long bookingId,
            String tripTitle, String tripDestination, int numberOfSeats,
            BigDecimal totalPrice, LocalDateTime bookingDate) {
        try {
            String subject = "Booking Confirmed - TravelMate AI";
            String htmlContent = buildConfirmationEmailTemplate(
                    userName, bookingId, tripTitle, tripDestination,
                    numberOfSeats, totalPrice, bookingDate);

            sendHtmlEmail(userEmail, subject, htmlContent);
            log.info("Booking confirmation email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email to {}: {}", userEmail, e.getMessage());
        }
    }

    /**
     * Send booking cancellation email asynchronously.
     */
    @Async("taskExecutor")
    public void sendCancellationAsync(String userName, String userEmail,
            Long bookingId, String tripTitle, BigDecimal totalPrice) {
        try {
            String subject = "Booking Cancelled - TravelMate AI";
            String htmlContent = buildCancellationEmailTemplate(
                    userName, bookingId, tripTitle, totalPrice);

            sendHtmlEmail(userEmail, subject, htmlContent);
            log.info("Booking cancellation email sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send booking cancellation email to {}: {}", userEmail, e.getMessage());
        }
    }

    /**
     * Send HTML email via SMTP.
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Build confirmation email HTML template.
     */
    private String buildConfirmationEmailTemplate(String userName, Long bookingId,
            String tripTitle, String tripDestination, int numberOfSeats,
            BigDecimal totalPrice, LocalDateTime bookingDate) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("en", "IN"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .booking-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                        .detail-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }
                        .detail-label { font-weight: bold; color: #666; }
                        .highlight { color: #667eea; font-weight: bold; }
                        .footer { text-align: center; padding: 20px; color: #888; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 Booking Confirmed!</h1>
                            <p>Thank you for choosing TravelMate AI</p>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>
                            <p>Your booking has been successfully confirmed. Here are your booking details:</p>

                            <div class="booking-details">
                                <div class="detail-row">
                                    <span class="detail-label">Booking ID:</span>
                                    <span class="highlight">#%d</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Trip:</span>
                                    <span>%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Destination:</span>
                                    <span>%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Number of Seats:</span>
                                    <span>%d</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Total Price:</span>
                                    <span class="highlight">%s</span>
                                </div>
                                <div class="detail-row">
                                    <span class="detail-label">Booking Date:</span>
                                    <span>%s</span>
                                </div>
                            </div>

                            <p>We look forward to providing you with an amazing travel experience!</p>
                            <p>Best regards,<br><strong>TravelMate AI Team</strong></p>
                        </div>
                        <div class="footer">
                            <p>This is an automated email. Please do not reply directly to this message.</p>
                            <p>© 2026 TravelMate AI. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        userName,
                        bookingId,
                        tripTitle,
                        tripDestination,
                        numberOfSeats,
                        currencyFormat.format(totalPrice),
                        bookingDate.format(dateFormatter));
    }

    /**
     * Build cancellation email HTML template.
     */
    private String buildCancellationEmailTemplate(String userName, Long bookingId,
            String tripTitle, BigDecimal totalPrice) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("en", "IN"));

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: #dc3545; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .booking-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                        .detail-row { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }
                        .footer { text-align: center; padding: 20px; color: #888; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Booking Cancelled</h1>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>
                            <p>Your booking has been cancelled as requested.</p>

                            <div class="booking-details">
                                <div class="detail-row">
                                    <span>Booking ID:</span>
                                    <span>#%d</span>
                                </div>
                                <div class="detail-row">
                                    <span>Trip:</span>
                                    <span>%s</span>
                                </div>
                                <div class="detail-row">
                                    <span>Refund Amount:</span>
                                    <span>%s</span>
                                </div>
                            </div>

                            <p>If you have any questions, please contact our support team.</p>
                            <p>Best regards,<br><strong>TravelMate AI Team</strong></p>
                        </div>
                        <div class="footer">
                            <p>© 2026 TravelMate AI. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        userName,
                        bookingId,
                        tripTitle,
                        currencyFormat.format(totalPrice));
    }
}
