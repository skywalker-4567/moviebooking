package com.utkarsh.moviebooking.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String userName,
        String movieTitle,
        int rating,
        String comment,
        LocalDateTime reviewedAt
) {}
