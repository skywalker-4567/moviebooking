package com.utkarsh.moviebooking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ShowResponse(
        Long showId,
        String movieTitle,
        String screenName,
        String theatreName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal basePrice
) {}
