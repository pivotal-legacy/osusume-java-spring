package com.tokyo.beach.application.restaurant;

import com.tokyo.beach.application.photos.NewPhotoUrl;

import java.util.List;
import java.util.Optional;

public class NewRestaurantWrapper {

    private NewRestaurant restaurant;

    @SuppressWarnings("unused")
    public NewRestaurantWrapper() {}

    @SuppressWarnings("unused")
    public NewRestaurantWrapper(NewRestaurant restaurant) {
        this.restaurant = restaurant;
    }

    public NewRestaurant getRestaurant() {
        return restaurant;
    }

    public String toString() {
        return restaurant.toString();
    }

    public List<NewPhotoUrl> getPhotoUrls() {
        return restaurant.getPhotoUrls();
    }

    public Optional<Integer> getCuisineId() {
        return restaurant.getCuisineId();
    }
}
