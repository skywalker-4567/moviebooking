package com.utkarsh.moviebooking.dto.request;

import com.utkarsh.moviebooking.enums.Genre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank String title,
        String description,
        @Min(1) int durationMinutes,
        String language,
        LocalDate releaseDate,
        Genre genre
) {}
