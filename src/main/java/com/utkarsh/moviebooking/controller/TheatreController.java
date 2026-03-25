package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.TheatreRequest;
import com.utkarsh.moviebooking.dto.response.TheatreResponse;
import com.utkarsh.moviebooking.security.CustomUserDetails;
import com.utkarsh.moviebooking.service.TheatreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
@RequiredArgsConstructor
@Tag(name = "Theatres")
public class TheatreController {

    private final TheatreService theatreService;

    @PostMapping
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<TheatreResponse> create(
            @RequestBody @Valid TheatreRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long ownerId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(theatreService.create(ownerId, request));
    }

    @GetMapping
    public ResponseEntity<Page<TheatreResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(theatreService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TheatreResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(theatreService.getById(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<List<TheatreResponse>> getMyTheatres(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long ownerId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.ok(theatreService.getMyTheatres(ownerId));
    }
}
