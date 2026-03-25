package com.utkarsh.moviebooking.dto.response;

import com.utkarsh.moviebooking.enums.Genre;

import java.time.LocalDate;

public record MovieResponse(
        Long id,
        String title,
        String description,
        int durationMinutes,
        String language,
        LocalDate releaseDate,
        Genre genre
) {}
