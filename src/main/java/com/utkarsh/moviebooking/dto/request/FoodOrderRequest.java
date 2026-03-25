package com.utkarsh.moviebooking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FoodOrderRequest(
        @NotNull Long bookingId,
        @NotEmpty List<FoodOrderItemRequest> items
) {}
