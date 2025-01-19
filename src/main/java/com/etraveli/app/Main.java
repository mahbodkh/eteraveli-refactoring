package com.etraveli.app;

import com.etraveli.app.model.Customer;
import com.etraveli.app.model.Movie;
import com.etraveli.app.model.MovieRental;
import com.etraveli.app.model.MovieType;
import com.etraveli.app.repository.InMemoryMovieRepository;
import com.etraveli.app.repository.MovieRepository;
import com.etraveli.app.service.RentalStatementService;

import java.util.Arrays;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        final Map<String, Movie> knownMovies = Map.of(
                "F001", new Movie("You've Got Mail", MovieType.REGULAR),
                "F002", new Movie("Matrix", MovieType.REGULAR),
                "F003", new Movie("Cars", MovieType.CHILDREN),
                "F004", new Movie("Fast & Furious X", MovieType.NEW)
        );
        final String statement = getStatement(knownMovies);

        System.out.println(statement);
    }

    private static String getStatement(Map<String, Movie> movieMap) {

        MovieRepository repository = new InMemoryMovieRepository(movieMap);

        RentalStatementService statementService = new RentalStatementService(repository);

        Customer customer = new Customer(
                "C. U. Stomer",
                Arrays.asList(
                        new MovieRental("F001", 3),
                        new MovieRental("F002", 1)
                )
        );

        return statementService.statement(customer);
    }
}
