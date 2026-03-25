package com.utkarsh.moviebooking.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Long movieId,
        @Min(1) @Max(5) int rating,
        String comment
) {}
