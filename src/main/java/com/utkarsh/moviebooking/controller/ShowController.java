package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.ShowRequest;
import com.utkarsh.moviebooking.dto.response.ShowResponse;
import com.utkarsh.moviebooking.service.ShowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Tag(name = "Shows")
public class ShowController {

    private final ShowService showService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('THEATRE_OWNER')")
    public ResponseEntity<ShowResponse> create(@RequestBody @Valid ShowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(showService.create(request));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Page<ShowResponse>> getShows(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(showService.getShowsByMovie(movieId, page, size));
    }
}