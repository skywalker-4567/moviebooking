package com.utkarsh.moviebooking.dto.request;

import com.utkarsh.moviebooking.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SeatRequest(
        @NotBlank String seatNumber,
        @NotNull SeatType seatType,
        @NotNull BigDecimal price,
        @NotNull Long screenId
) {}
