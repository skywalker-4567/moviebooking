package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendBookingConfirmation(Booking booking) {
        try {
            String subject = "Booking Confirmed — " + booking.getShow().getMovie().getTitle();
            String body = buildConfirmationBody(booking);
            sendEmail(booking.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send confirmation email: {}", e.getMessage());
        }
    }

    @Async
    public void sendCancellationEmail(Booking booking, BigDecimal refundAmount) {
        try {
            String subject = "Booking Cancelled — " + booking.getShow().getMovie().getTitle();
            String body = buildCancellationBody(booking, refundAmount);
            sendEmail(booking.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send cancellation email: {}", e.getMessage());
        }
    }

    @Async
    public void sendShowReminder(Booking booking) {
        try {
            String subject = "Reminder — Your show is tomorrow!";
            String body = buildReminderBody(booking);
            sendEmail(booking.getUser().getEmail(), subject, body);
        } catch (Exception e) {
            log.warn("Failed to send reminder email: {}", e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private String buildConfirmationBody(Booking booking) {
        List<String> seats = booking.getShowSeats().stream()
                .map(ss -> ss.getSeat().getSeatNumber())
                .toList();
        return """
            Hi %s,

            Your booking is confirmed!

            Movie   : %s
            Date    : %s
            Seats   : %s
            Total   : ₹%s
            Booking : #%d

            Enjoy the show!
            """.formatted(
                booking.getUser().getName(),
                booking.getShow().getMovie().getTitle(),
                booking.getShow().getStartTime(),
                String.join(", ", seats),
                booking.getTotalAmount(),
                booking.getId()
        );
    }

    private String buildCancellationBody(Booking booking, BigDecimal refundAmount) {
        return """
            Hi %s,

            Your booking #%d for "%s" has been cancelled.

            Amount Paid : ₹%s
            Refund      : ₹%s

            Refunds are processed within 5-7 business days.
            """.formatted(
                booking.getUser().getName(),
                booking.getId(),
                booking.getShow().getMovie().getTitle(),
                booking.getTotalAmount(),
                refundAmount
        );
    }

    private String buildReminderBody(Booking booking) {
        List<String> seats = booking.getShowSeats().stream()
                .map(ss -> ss.getSeat().getSeatNumber())
                .toList();
        return """
            Hi %s,

            Just a reminder — your show is tomorrow!

            Movie  : %s
            Time   : %s
            Seats  : %s

            See you there!
            """.formatted(
                booking.getUser().getName(),
                booking.getShow().getMovie().getTitle(),
                booking.getShow().getStartTime(),
                String.join(", ", seats)
        );
    }
}