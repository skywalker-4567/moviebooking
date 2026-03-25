package com.utkarsh.moviebooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FoodOrderItemRequest(
        @NotNull Long foodItemId,
        @Min(1) int quantity
) {}
