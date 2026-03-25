package com.utkarsh.moviebooking.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TheatreRequest(
        @NotBlank String name,
        @NotBlank String city,
        String address
) {}
