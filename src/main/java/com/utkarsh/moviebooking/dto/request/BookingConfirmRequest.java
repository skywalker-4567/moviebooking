package com.utkarsh.moviebooking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingConfirmRequest(
        @NotNull Long showId,
        @NotEmpty List<Long> showSeatIds
) {}
