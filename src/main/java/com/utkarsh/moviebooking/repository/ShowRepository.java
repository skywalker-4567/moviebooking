package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieIdAndScreenId(Long movieId, Long screenId);
    // ShowRepository
    Page<Show> findByMovieId(Long movieId, Pageable pageable);
}
