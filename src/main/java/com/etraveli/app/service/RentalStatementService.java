package com.etraveli.app.service;

import com.etraveli.app.model.Customer;
import com.etraveli.app.model.Movie;
import com.etraveli.app.model.MovieRental;
import com.etraveli.app.repository.MovieRepository;
import com.etraveli.app.service.dto.RentalDetails;


public class RentalStatementService {
    private final MovieRepository movieRepository;

    public RentalStatementService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    public String statement(Customer customer) {
        double totalCharge = 0.0;
        int totalPoints = 0;

        final StringBuilder statementBuilder = new StringBuilder();
        statementBuilder
                .append("Rental Record for ")
                .append(customer.getName())
                .append("\n");


        for (MovieRental rental : customer.getRentals()) {
            final RentalDetails details = processRental(rental, statementBuilder);
            totalCharge += details.charge();
            totalPoints += details.points();
        }

        statementBuilder.append("Amount owed is ")
                .append(totalCharge).append("\n")
                .append("You earned ")
                .append(totalPoints)
                .append(" frequent points").append("\n");

        return statementBuilder.toString();
    }


    private RentalDetails processRental(final MovieRental rental, final StringBuilder statementBuilder) {
        final Movie movie = movieRepository.findMovieById(rental.getMovieId());

        if (movie == null) {
            throw new IllegalArgumentException("Unknown movie ID: " + rental.getMovieId());
        }

        final double charge = PriceCalculatorService.calculateCharge(movie.getType(), rental.getDays());
        final int points = PriceCalculatorService.calculateFrequentRenterPoints(movie.getType(), rental.getDays());

        statementBuilder
                .append("\t")
                .append(movie.getTitle())
                .append("\t")
                .append(charge)
                .append("\n");

        return new RentalDetails(charge, points);
    }
}
