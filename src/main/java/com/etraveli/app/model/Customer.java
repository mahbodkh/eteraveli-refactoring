package com.etraveli.app.model;

import java.util.ArrayList;
import java.util.List;

public class Customer {

    private final String name;
    private final List<MovieRental> rentals;

    public Customer(String name, List<MovieRental> rentals) {
        this.name = name;
        this.rentals = rentals;
    }

    public String getName() {
        return name;
    }

    public List<MovieRental> getRentals() {
        return rentals;
    }


    public static class CustomerBuilder {
        private String name;
        private final List<MovieRental> rentals = new ArrayList<>();

        public CustomerBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CustomerBuilder withRental(List<MovieRental> rentals) {
            this.rentals.addAll(rentals);
            return this;
        }

        public Customer build() {
            return new Customer(name, rentals);
        }
    }
}
