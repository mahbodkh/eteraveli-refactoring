package com.etraveli.app.repository;

import com.etraveli.app.model.Movie;

public interface MovieRepository {

    Movie findMovieById(String movieId);
}
