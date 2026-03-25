package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.BookingConfirmRequest;
import com.utkarsh.moviebooking.dto.request.SeatHoldRequest;
import com.utkarsh.moviebooking.dto.response.BookingResponse;
import com.utkarsh.moviebooking.dto.response.CancellationResponse;
import com.utkarsh.moviebooking.security.CustomUserDetails;
import com.utkarsh.moviebooking.service.BookingService;
import com.utkarsh.moviebooking.service.CancellationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final CancellationService cancellationService;
    // Hold seats for 10 minutes
    @PostMapping("/hold")
    public ResponseEntity<String> holdSeats(
            @RequestBody @Valid SeatHoldRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        bookingService.holdSeats(userId, request);
        return ResponseEntity.ok("Seats held for 10 minutes. Proceed to payment.");
    }

    // Confirm after payment
    @PostMapping("/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @RequestBody @Valid BookingConfirmRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        BookingResponse response = bookingService.confirmBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get user's bookings
    // BookingController
    @GetMapping("/my")
    public ResponseEntity<Page<BookingResponse>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookedAt").descending());
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId, pageable));
    }
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<CancellationResponse> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(cancellationService.cancelBooking(bookingId, userId));
    }

}
