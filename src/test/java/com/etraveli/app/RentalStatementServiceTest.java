package com.etraveli.app;


import com.etraveli.app.model.Customer;
import com.etraveli.app.model.Movie;
import com.etraveli.app.model.MovieRental;
import com.etraveli.app.model.MovieType;
import com.etraveli.app.repository.InMemoryMovieRepository;
import com.etraveli.app.repository.MovieRepository;
import com.etraveli.app.service.RentalStatementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RentalStatementServiceTest {

    private final static String CUSTOMER_NAME = "Customer_Name";
    private RentalStatementService service;


    @BeforeEach
    void setUp() {
        final Map<String, Movie> movies = Map.of(
                "F001", new Movie("You've Got Mail", MovieType.REGULAR),
                "F002", new Movie("Matrix", MovieType.REGULAR),
                "F003", new Movie("Cars", MovieType.CHILDREN),
                "F004", new Movie("Fast & Furious X", MovieType.NEW)
        );

        MovieRepository repository = new InMemoryMovieRepository(movies);

        service = new RentalStatementService(repository);
    }

    @Test
    @DisplayName("Should match expected output for 'regular' movie rental")
    void statement_shouldMatchExpectedOutput() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F001", 3),
                        new MovieRental("F002", 1)
                )
        );

        // when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of("You've Got Mail", "Matrix"), List.of(3.5, 2.0), 5.5, 2);
        assertEquals(expected, actual, "The generated statement should match the expected output.");
    }

    @Test
    @DisplayName("Should match expected output for 'new release' movie rental")
    void statement_shouldMatchExpectedOutputForNewRelease() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F004", 3)
                )
        );

        // when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of("Fast & Furious X"), List.of(9.0), 9.0, 2);
        assertEquals(expected, actual, "The generated statement should match the expected output for new release.");
    }

    @Test
    @DisplayName("Should match expected output for 'children's' movie rental")
    void statement_shouldMatchExpectedOutputForChildrenMovie() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F003", 1)
                )
        );

        // when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of("Cars"), List.of(1.5), 1.5, 1);
        assertEquals(expected, actual, "The generated statement should match the expected output for children's movie.");
    }

    @Test
    @DisplayName("Should match expected output for multiple rentals")
    void statement_shouldMatchExpectedOutputForMultipleRentals() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F001", 3),
                        new MovieRental("F002", 1),
                        new MovieRental("F003", 1),
                        new MovieRental("F004", 3)
                )
        );

        // when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of("You've Got Mail", "Matrix", "Cars", "Fast & Furious X"), List.of(3.5, 2.0, 1.5, 9.0), 16.0, 5);
        assertEquals(expected, actual, "The generated statement should match the expected output for multiple rentals.");
    }

    @Test
    @DisplayName("Should match expected output for zero days rental")
    void statement_shouldMatchExpectedOutputForZeroDaysRental() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F001", 0)
                )
        );

        //when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of("You've Got Mail"), List.of(2.0), 2.0, 1);
        assertEquals(expected, actual, "The generated statement should match the expected output for zero days rental.");
    }

    @Test
    @DisplayName("Should match expected output for large number of days rental")
    void statement_shouldMatchExpectedOutputForLargeNumberOfDaysRental() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F001", 10)
                )
        );

        // when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of("You've Got Mail"), List.of(14.0), 14.0, 1);
        assertEquals(expected, actual, "The generated statement should match the expected output for large number of days rental.");
    }

    @Test
    @DisplayName("Should match expected output for empty rental list")
    void statement_shouldMatchExpectedOutputForEmptyRentalList() {
        // given
        Customer customer = createCustomer(CUSTOMER_NAME, List.of());

        // when
        String actual = service.statement(customer);

        // then
        String expected = getExpectedMessage(CUSTOMER_NAME, List.of(), List.of(), 0.0, 0);
        assertEquals(expected, actual, "The generated statement should match the expected output for empty rental list.");
    }

    @Test
    @DisplayName("Should throw exception for invalid movie ID")
    void statement_shouldHandleInvalidMovieId() {
        // given
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("INVALID_ID", 3)
                )
        );

        // then
        assertThrows(IllegalArgumentException.class,
                () -> service.statement(customer),
                "Unknown movie ID: INVALID_ID"
        );
    }

    @Test
    @DisplayName("Should throw exception for negative days rental")
    void statement_shouldHandleNegativeDaysRental() {
        // given
        int daysRented = -1;
        Customer customer = createCustomer(
                CUSTOMER_NAME,
                List.of(
                        new MovieRental("F001", daysRented)
                )
        );

        // then
        assertThrows(IllegalArgumentException.class,
                () -> service.statement(customer),
                String.format("daysRented cannot be negative: %s", daysRented)
        );
    }


    private Customer createCustomer(String name, List<MovieRental> rentals) {
        return new Customer.CustomerBuilder()
                .withName(name)
                .withRental(rentals)
                .build();
    }

    private static String getExpectedMessage(String customerName,
                                             List<String> movieTitles,
                                             List<Double> charges,
                                             double totalAmount,
                                             int totalFrequentRenterPoints) {

        final StringBuilder sb = new StringBuilder()
                .append("Rental Record for ")
                .append(customerName)
                .append("\n");
        for (int i = 0; i < movieTitles.size(); i++) {
            sb.append("\t")
                    .append(movieTitles.get(i)).append("\t")
                    .append(charges.get(i)).append("\n");
        }
        sb.append("Amount owed is ")
                .append(totalAmount).append("\n");
        sb.append("You earned ")
                .append(totalFrequentRenterPoints)
                .append(" frequent points").append("\n");
        return sb.toString();
    }
}
