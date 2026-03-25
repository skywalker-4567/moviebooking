package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.RefundPolicyCalculator;
import com.utkarsh.moviebooking.dto.response.CancellationResponse;
import com.utkarsh.moviebooking.entity.Booking;
import com.utkarsh.moviebooking.entity.Payment;
import com.utkarsh.moviebooking.enums.BookingStatus;
import com.utkarsh.moviebooking.enums.PaymentStatus;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.exception.UnauthorizedException;
import com.utkarsh.moviebooking.repository.BookingRepository;
import com.utkarsh.moviebooking.repository.PaymentRepository;
import com.utkarsh.moviebooking.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancellationService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ShowSeatRepository showSeatRepository;
    private final RefundPolicyCalculator refundPolicyCalculator;

    private final EmailService emailService;
    @Transactional
    public CancellationResponse cancelBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Ownership check
        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this booking");
        }

        // Can only cancel CONFIRMED bookings
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }

        LocalDateTime showTime = booking.getShow().getStartTime();

        // Cannot cancel after show has started
        if (LocalDateTime.now().isAfter(showTime)) {
            throw new IllegalStateException("Cannot cancel a booking after the show has started");
        }

        // Calculate refund
        BigDecimal refundAmount = refundPolicyCalculator
                .calculate(booking.getTotalAmount(), showTime);

        // Update booking
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Release seats back
        booking.getShowSeats().forEach(ss -> ss.setBooked(false));
        showSeatRepository.saveAll(booking.getShowSeats());

        // Update payment record
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        payment.setRefundAmount(refundAmount);
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        emailService.sendCancellationEmail(booking, refundAmount);

        return new CancellationResponse(
                bookingId,
                BookingStatus.CANCELLED,
                booking.getTotalAmount(),
                refundAmount,
                LocalDateTime.now()
        );
    }
}
