package com.utkarsh.moviebooking.controller;

import com.utkarsh.moviebooking.dto.request.ReviewRequest;
import com.utkarsh.moviebooking.dto.response.ReviewResponse;
import com.utkarsh.moviebooking.security.CustomUserDetails;
import com.utkarsh.moviebooking.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
            @RequestBody @Valid ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.submitReview(userId, request));
    }

    // ReviewController
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("reviewedAt").descending());
        return ResponseEntity.ok(reviewService.getReviewsForMovie(movieId, pageable));
    }
}
