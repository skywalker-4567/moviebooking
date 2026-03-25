package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.FoodOrderItemRequest;
import com.utkarsh.moviebooking.dto.request.FoodOrderRequest;
import com.utkarsh.moviebooking.dto.response.FoodOrderResponse;
import com.utkarsh.moviebooking.entity.Booking;
import com.utkarsh.moviebooking.entity.FoodItem;
import com.utkarsh.moviebooking.entity.FoodOrder;
import com.utkarsh.moviebooking.enums.BookingStatus;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.exception.UnauthorizedException;
import com.utkarsh.moviebooking.repository.BookingRepository;
import com.utkarsh.moviebooking.repository.FoodItemRepository;
import com.utkarsh.moviebooking.repository.FoodOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodOrderService {

    private final FoodOrderRepository foodOrderRepository;
    private final FoodItemRepository foodItemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public List<FoodOrderResponse> placeOrder(Long userId, FoodOrderRequest request) {

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!((Booking) booking).getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this booking");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Food orders only allowed on confirmed bookings");
        }

        List<FoodOrder> orders = new ArrayList<>();

        for (FoodOrderItemRequest item : request.items()) {
            FoodItem foodItem = foodItemRepository.findById(item.foodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Food item not found: " + item.foodItemId()));

            if (!foodItem.isAvailable()) {
                throw new IllegalStateException(foodItem.getName() + " is not available");
            }

            BigDecimal subtotal = foodItem.getPrice()
                    .multiply(BigDecimal.valueOf(item.quantity()));

            orders.add(FoodOrder.builder()
                    .booking(booking)
                    .foodItem(foodItem)
                    .quantity(item.quantity())
                    .subtotal(subtotal)
                    .build());
        }

        List<FoodOrder> saved = foodOrderRepository.saveAll(orders);

        return saved.stream().map(o -> new FoodOrderResponse(
                o.getId(),
                o.getFoodItem().getName(),
                o.getQuantity(),
                o.getSubtotal()
        )).toList();
    }

    public List<FoodOrderResponse> getOrdersByBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this booking");
        }

        return foodOrderRepository.findByBookingId(bookingId).stream()
                .map(o -> new FoodOrderResponse(
                        o.getId(),
                        o.getFoodItem().getName(),
                        o.getQuantity(),
                        o.getSubtotal()
                )).toList();
    }
}
