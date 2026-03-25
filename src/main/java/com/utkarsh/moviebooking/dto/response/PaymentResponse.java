package com.utkarsh.moviebooking.dto.response;

import com.utkarsh.moviebooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long bookingId,
        BigDecimal amount,
        BigDecimal refundAmount,
        PaymentStatus status,
        LocalDateTime paidAt,
        LocalDateTime refundedAt
) {}
