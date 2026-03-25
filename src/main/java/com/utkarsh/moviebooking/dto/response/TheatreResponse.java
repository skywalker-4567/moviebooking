package com.utkarsh.moviebooking.dto.response;
public record TheatreResponse(
        Long id,
        String name,
        String city,
        String address,
        String ownerName
) {}
