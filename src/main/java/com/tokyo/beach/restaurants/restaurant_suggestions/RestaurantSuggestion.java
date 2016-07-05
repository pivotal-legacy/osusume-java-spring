package com.tokyo.beach.restaurants.restaurant_suggestions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestaurantSuggestion {
    private String placeId;
    private String name;
    private String address;
    private Geometry geometry;

    public RestaurantSuggestion() { }

    public RestaurantSuggestion(String placeId, String name, String address, Geometry geometry) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("formatted_address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("geometry")
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @JsonProperty("place_id")
    public String getPlaceId() {
        return placeId;
    }

    public double getLatitude() {
        return geometry
                .getLocation()
                .getLatitude();
    }

    public double getLongitude() {
        return geometry
                .getLocation()
                .getLongitude();
    }
}
