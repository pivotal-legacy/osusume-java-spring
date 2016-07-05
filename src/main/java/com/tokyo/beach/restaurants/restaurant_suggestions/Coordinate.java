package com.tokyo.beach.restaurants.restaurant_suggestions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinate {
    @JsonProperty("lat")
    private double latitude;

    @JsonProperty("lng")
    private double longitude;

    public Coordinate() {}

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
