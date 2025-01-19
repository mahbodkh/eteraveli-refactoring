package com.etraveli.app.repository;

import com.etraveli.app.model.Movie;

import java.util.Map;

public class InMemoryMovieRepository implements MovieRepository {

    private final Map<String, Movie> movies;

    public InMemoryMovieRepository(Map<String, Movie> movies) {
        this.movies = movies;
    }

    @Override
    public Movie findMovieById(String movieId) {
        return movies.get(movieId);
    }
}
