package com.utkarsh.moviebooking.dto.response;
public record AuthResponse(
        String token,
        String email,
        String role
) {}
