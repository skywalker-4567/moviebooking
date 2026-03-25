package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
    // ReviewRepository
    Page<Review> findByMovieId(Long movieId, Pageable pageable);
    Double findAverageRatingByMovieId(Long movieId);

    // Check if user actually watched the movie (has a confirmed booking for it)
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        JOIN b.show s
        WHERE b.user.id = :userId
        AND s.movie.id = :movieId
        AND b.status = 'CONFIRMED'
        AND s.endTime < CURRENT_TIMESTAMP
    """)
    boolean hasUserWatchedMovie(@Param("userId") Long userId,
                                @Param("movieId") Long movieId);
}
