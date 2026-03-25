package com.utkarsh.moviebooking.repository;

import com.utkarsh.moviebooking.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByOwnerId(Long ownerId);
}
