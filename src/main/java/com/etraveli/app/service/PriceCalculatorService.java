package com.etraveli.app.service;


import com.etraveli.app.model.MovieType;

public class PriceCalculatorService {

    private final static int NEW_RELEASE_THRESHOLD_DAYS = 2;

    private PriceCalculatorService() {
    }

    /**
     * Calculates the charge.
     */
    public static double calculateCharge(final MovieType type, final int daysRented) {
        if (daysRented < 0) {
            throw new IllegalArgumentException("daysRented cannot be negative: " + daysRented);
        }
        return switch (type) {
            case REGULAR, CHILDREN -> calculateThresholdCharge(
                    type.getDailyRate(),
                    type.getExtraPrice(),
                    type.getThresholdDays(),
                    daysRented
            );
            case NEW -> daysRented * type.getDailyRate();
        };
    }

    /**
     * Calculates the frequent renter by points.
     */
    public static int calculateFrequentRenterPoints(final MovieType type, final int daysRented) {
        // Ensure days rented is not negative
        final int validDaysRented = Math.max(0, daysRented);

        int points = 1;
        if (MovieType.NEW.equals(type) && validDaysRented > NEW_RELEASE_THRESHOLD_DAYS) {
            points += 1; // Bonus point for renting a new release more than 2 days
        }
        return points;
    }

    /**
     * calculate base + extras days
     */
    private static double calculateThresholdCharge(
            double basePrice,
            double extraPricePerDay,
            int thresholdDays,
            int daysRented) {

        double amount = basePrice;

        if (daysRented > thresholdDays) {
            amount += (daysRented - thresholdDays) * extraPricePerDay;
        }
        return amount;
    }
}
