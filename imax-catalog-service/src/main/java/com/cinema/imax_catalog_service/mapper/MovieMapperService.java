package com.cinema.imax_catalog_service.mapper;

import com.cinema.imax_catalog_service.dto.response.MovieResponse;
import com.cinema.imax_catalog_service.model.Movie;
import org.springframework.stereotype.Service;

@Service
public class MovieMapperService {

    public MovieResponse convertToDto(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .originalTitle(movie.getOriginalTitle())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .posterPath(movie.getPosterPath())
                .genreIds(movie.getGenreIds())
                .build();
    }

    public Movie convertToEntity(MovieResponse dto) {
        return Movie.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .originalTitle(dto.getOriginalTitle())
                .overview(dto.getOverview())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .rating(dto.getRating())
                .posterPath(dto.getPosterPath())
                .genreIds(dto.getGenreIds())
                .build();
    }
}