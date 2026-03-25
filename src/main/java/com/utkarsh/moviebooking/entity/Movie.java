package com.utkarsh.moviebooking.entity;

import com.utkarsh.moviebooking.enums.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;
    private int durationMinutes;
    private String language;
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private Genre genre;
}
