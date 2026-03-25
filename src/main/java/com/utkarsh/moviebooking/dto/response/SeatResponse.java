package com.utkarsh.moviebooking.dto.response;

import com.utkarsh.moviebooking.enums.SeatType;

import java.math.BigDecimal;

public record SeatResponse(
        Long id,
        String seatNumber,
        SeatType seatType,
        BigDecimal price,
        Long screenId
) {}
