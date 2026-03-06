package com.travelmateai.backend.service;

import com.travelmateai.backend.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for sending email notifications.
 * Extracts data from entities before the async boundary to avoid
 * LazyInitializationException on detached Hibernate proxies.
 * Delegates actual async sending to EmailAsyncHelper.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final EmailAsyncHelper emailAsyncHelper;

    /**
     * Send booking confirmation email.
     * Extracts all needed data while Hibernate session is still open,
     * then delegates to the async helper for non-blocking email delivery.
     */
    public void sendBookingConfirmationEmail(Booking booking) {
        // Extract data while Hibernate session is still open
        String userName = booking.getUser().getName();
        String userEmail = booking.getUser().getEmail();
        Long bookingId = booking.getId();
        String tripTitle = booking.getTrip().getTitle();
        String tripDestination = booking.getTrip().getDestination();
        int numberOfSeats = booking.getNumberOfSeats();
        BigDecimal totalPrice = booking.getTotalPrice();
        LocalDateTime bookingDate = booking.getBookingDate();

        // Delegate to async helper (Spring AOP proxy will correctly intercept @Async)
        emailAsyncHelper.sendConfirmationAsync(userName, userEmail, bookingId, tripTitle,
                tripDestination, numberOfSeats, totalPrice, bookingDate);
    }

    /**
     * Send booking cancellation email.
     * Extracts all needed data while Hibernate session is still open,
     * then delegates to the async helper for non-blocking email delivery.
     */
    public void sendBookingCancellationEmail(Booking booking) {
        // Extract data while Hibernate session is still open
        String userName = booking.getUser().getName();
        String userEmail = booking.getUser().getEmail();
        Long bookingId = booking.getId();
        String tripTitle = booking.getTrip().getTitle();
        BigDecimal totalPrice = booking.getTotalPrice();

        // Delegate to async helper (Spring AOP proxy will correctly intercept @Async)
        emailAsyncHelper.sendCancellationAsync(userName, userEmail, bookingId, tripTitle, totalPrice);
    }
}
