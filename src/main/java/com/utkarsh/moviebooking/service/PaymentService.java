package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.entity.Booking;
import com.utkarsh.moviebooking.entity.Payment;
import com.utkarsh.moviebooking.enums.PaymentStatus;
import com.utkarsh.moviebooking.repository.BookingRepository;
import com.utkarsh.moviebooking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Payment recordPayment(Booking booking) {
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .refundAmount(BigDecimal.ZERO)
                .status(PaymentStatus.COMPLETED)
                .paidAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    public Optional<Payment> getPaymentByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}
