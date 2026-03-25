package com.utkarsh.moviebooking.dto.response;

import java.math.BigDecimal;

public record FoodOrderResponse(
        Long foodOrderId,
        String itemName,
        int quantity,
        BigDecimal subtotal
) {}
