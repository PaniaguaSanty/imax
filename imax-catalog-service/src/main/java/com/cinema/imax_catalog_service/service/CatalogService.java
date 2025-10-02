package com.cinema.imax_catalog_service.service;

import com.cinema.imax_catalog_service.dto.response.MovieResponse;
import com.cinema.imax_catalog_service.mapper.MovieMapperService;
import com.cinema.imax_catalog_service.model.Movie;
import com.cinema.imax_catalog_service.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MovieRepository movieRepository;
    private final MovieMapperService movieMapper;

    public List<MovieResponse> getNowPlayingMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(movieMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<MovieResponse> getMovieById(Integer id) {
        return movieRepository.findById(id)
                .map(movieMapper::convertToDto);
    }

    public boolean isMovieInCatalog(Integer id) {
        return movieRepository.existsById(id);
    }
}
