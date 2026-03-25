package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.ReviewRequest;
import com.utkarsh.moviebooking.dto.response.ReviewResponse;
import com.utkarsh.moviebooking.entity.Movie;
import com.utkarsh.moviebooking.entity.Review;
import com.utkarsh.moviebooking.entity.User;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.repository.MovieRepository;
import com.utkarsh.moviebooking.repository.ReviewRepository;
import com.utkarsh.moviebooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse submitReview(Long userId, ReviewRequest request) {

        // Must have a confirmed booking for a show that already ended
        if (!reviewRepository.hasUserWatchedMovie(userId, request.movieId())) {
            throw new IllegalStateException(
                    "You can only review a movie after watching it"
            );
        }

        // One review per user per movie
        if (reviewRepository.existsByUserIdAndMovieId(userId, request.movieId())) {
            throw new IllegalStateException(
                    "You have already reviewed this movie"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Review review = Review.builder()
                .user(user)
                .movie(movie)
                .rating(request.rating())
                .comment(request.comment())
                .reviewedAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                user.getName(),
                movie.getTitle(),
                saved.getRating(),
                saved.getComment(),
                saved.getReviewedAt()
        );
    }

    public Page<ReviewResponse> getReviewsForMovie(Long movieId, Pageable pageable) {
        return reviewRepository.findByMovieId(movieId, pageable)
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getUser().getName(),
                        r.getMovie().getTitle(),
                        r.getRating(),
                        r.getComment(),
                        r.getReviewedAt()
                ));
    }
}
