package com.utkarsh.moviebooking.dto.response;

import com.utkarsh.moviebooking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CancellationResponse(
        Long bookingId,
        BookingStatus status,
        BigDecimal amountPaid,
        BigDecimal refundAmount,
        LocalDateTime cancelledAt
) {}
