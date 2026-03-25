package com.utkarsh.moviebooking.dto.response;
public record ScreenResponse(
        Long id,
        String name,
        int totalSeats,
        Long theatreId,
        String theatreName
) {}

