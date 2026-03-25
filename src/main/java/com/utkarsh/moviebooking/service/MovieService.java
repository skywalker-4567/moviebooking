package com.utkarsh.moviebooking.service;

import com.utkarsh.moviebooking.dto.request.MovieRequest;
import com.utkarsh.moviebooking.dto.response.MovieResponse;
import com.utkarsh.moviebooking.entity.Movie;
import com.utkarsh.moviebooking.exception.ResourceNotFoundException;
import com.utkarsh.moviebooking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieResponse create(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .durationMinutes(request.durationMinutes())
                .language(request.language())
                .releaseDate(request.releaseDate())
                .genre(request.genre())
                .build();
        return mapToResponse(movieRepository.save(movie));
    }

    public Page<MovieResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return movieRepository.findAll(pageable).map(this::mapToResponse);
    }

    public MovieResponse getById(Long id) {
        return mapToResponse(movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found")));
    }

    private MovieResponse mapToResponse(Movie m) {
        return new MovieResponse(m.getId(), m.getTitle(), m.getDescription(),
                m.getDurationMinutes(), m.getLanguage(), m.getReleaseDate(), m.getGenre());
    }
}
