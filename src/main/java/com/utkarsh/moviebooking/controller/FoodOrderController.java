package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.FoodOrderRequest;
import com.utkarsh.moviebooking.dto.response.FoodOrderResponse;
import com.utkarsh.moviebooking.entity.FoodItem;
import com.utkarsh.moviebooking.repository.FoodItemRepository;
import com.utkarsh.moviebooking.security.CustomUserDetails;
import com.utkarsh.moviebooking.service.FoodOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodOrderController {

    private final FoodOrderService foodOrderService;
    private final FoodItemRepository foodItemRepository;

    // Browse menu for a theatre
    @GetMapping("/menu/{theatreId}")
    public ResponseEntity<List<FoodItem>> getMenu(@PathVariable Long theatreId) {
        return ResponseEntity.ok(
                foodItemRepository.findByTheatreIdAndAvailableTrue(theatreId)
        );
    }

    // Place food order against a booking
    @PostMapping("/order")
    public ResponseEntity<List<FoodOrderResponse>> placeOrder(
            @RequestBody @Valid FoodOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(foodOrderService.placeOrder(userId, request));
    }

    // Get food orders for a booking
    @GetMapping("/order/booking/{bookingId}")
    public ResponseEntity<List<FoodOrderResponse>> getOrders(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(foodOrderService.getOrdersByBooking(bookingId, userId));
    }
}
