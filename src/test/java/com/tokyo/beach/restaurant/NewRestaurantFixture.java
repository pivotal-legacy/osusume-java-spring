package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;

import java.util.List;

public class NewRestaurantFixture {
    private Long priceRangeId = 0L;
    private Long cuisineId = null;
    private String name = "Not Specified";
    private String address = "address";
    private String nearestStation = "nearestStation";
    private String placeId = "place-id";
    private double latitude = 1.23;
    private double longitude = 2.34;
    private List<NewPhotoUrl> photoUrls = null;
    private String notes = "notes";

    public NewRestaurantFixture withPriceRangeId(Long priceRangeId) {
        this.priceRangeId = priceRangeId;
        return this;
    }

    public NewRestaurantFixture withCuisineId(long cuisineId) {
        this.cuisineId = cuisineId;
        return this;
    }

    public NewRestaurantFixture withName(String name) {
        this.name = name;
        return this;
    }

    public NewRestaurantFixture withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public NewRestaurantFixture withAddress(String address) {
        this.address = address;
        return this;
    }

    public NewRestaurantFixture withNearestStation(String nearestStation) {
        this.nearestStation = nearestStation;
        return this;
    }

    public NewRestaurantFixture withPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public NewRestaurantFixture withLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public NewRestaurantFixture withLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public NewRestaurantFixture withPhotoUrls(List<NewPhotoUrl> photoUrls) {
        this.photoUrls = photoUrls;
        return this;
    }

    public NewRestaurant build() {
        return new NewRestaurant(
                name,
                address,
                nearestStation,
                placeId,
                latitude,
                longitude,
                notes,
                cuisineId,
                priceRangeId,
                photoUrls
        );
    }
}
