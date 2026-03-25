package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.SeatRequest;
import com.utkarsh.moviebooking.dto.response.SeatResponse;
import com.utkarsh.moviebooking.service.SeatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
@Tag(name = "Seats")
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatResponse> create(@RequestBody @Valid SeatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatService.create(request));
    }

    @GetMapping("/screen/{screenId}")
    public ResponseEntity<List<SeatResponse>> getByScreen(@PathVariable Long screenId) {
        return ResponseEntity.ok(seatService.getByScreen(screenId));
    }
}
