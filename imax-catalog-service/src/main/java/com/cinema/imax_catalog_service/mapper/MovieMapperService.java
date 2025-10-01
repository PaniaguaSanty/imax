package com.cinema.imax_catalog_service.mapper;

import com.cinema.imax_catalog_service.client.MapperMethods;
import com.cinema.imax_catalog_service.model.Movie;
import com.cinema.imax_catalog_service.dto.response.MovieResponse;
import org.springframework.stereotype.Service;

@Service
public class MovieMapperService implements MapperMethods<MovieResponse, Movie> {

    @Override
    public Movie convertToEntity(MovieResponse response) {
        if (response == null) return null;

        return Movie.builder()
                .id(response.getId())
                .title(response.getTitle())
                .overview(response.getOverview())
                .releaseDate(response.getReleaseDate())
                .posterPath(response.getPosterPath())
                .genreIds(response.getGenreIds())
                .build();
    }

    @Override
    public MovieResponse convertToDto(Movie entity) {
        if (entity == null) return null;

        return MovieResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .overview(entity.getOverview())
                .releaseDate(entity.getReleaseDate())
                .posterPath(entity.getPosterPath())
                .genreIds(entity.getGenreIds())
                .build();
    }
}
