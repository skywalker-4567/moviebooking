package com.utkarsh.moviebooking.dto.response;

import com.utkarsh.moviebooking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String movieTitle,
        LocalDateTime showTime,
        List<String> seatNumbers,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime bookedAt
) {}
