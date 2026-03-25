package com.utkarsh.moviebooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScreenRequest(
        @NotBlank String name,
        @Min(1) int totalSeats,
        @NotNull Long theatreId
) {}
