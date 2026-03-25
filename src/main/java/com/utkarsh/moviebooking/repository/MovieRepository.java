package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {}
