package com.etraveli.app.model;

public enum MovieType {

    REGULAR(2.0, 1.5, 2),
    NEW(3.0, 0.0, 0),
    CHILDREN(1.5, 1.5, 3);

    private final double DailyRate;
    private final double extraPrice;
    private final int thresholdDays;

    MovieType(double DailyRate, double extraPrice, int thresholdDays) {
        this.DailyRate = DailyRate;
        this.extraPrice = extraPrice;
        this.thresholdDays = thresholdDays;
    }


    public double getDailyRate() {
        return DailyRate;
    }

    public double getExtraPrice() {
        return extraPrice;
    }

    public int getThresholdDays() {
        return thresholdDays;
    }
}

