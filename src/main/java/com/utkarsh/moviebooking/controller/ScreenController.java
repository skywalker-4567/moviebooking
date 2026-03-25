package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.ScreenRequest;
import com.utkarsh.moviebooking.dto.response.ScreenResponse;
import com.utkarsh.moviebooking.service.ScreenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
@Tag(name = "Screens")
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenResponse> create(@RequestBody @Valid ScreenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(screenService.create(request));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<ScreenResponse>> getByTheatre(@PathVariable Long theatreId) {
        return ResponseEntity.ok(screenService.getByTheatre(theatreId));
    }
}
