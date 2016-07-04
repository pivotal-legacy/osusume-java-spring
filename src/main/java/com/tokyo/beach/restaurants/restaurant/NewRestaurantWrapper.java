package com.tokyo.beach.restaurants.restaurant;

import com.tokyo.beach.restaurants.photos.NewPhotoUrl;

import java.util.List;

@SuppressWarnings("WeakerAccess")
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

    public List<NewPhotoUrl> getPhotoUrls() {
        return restaurant.getPhotoUrls();
    }

    public Long getCuisineId() {
        return restaurant.getCuisineId();
    }

    public Long getPriceRangeId() {
        return restaurant.getPriceRangeId();
    }

    public String toString() {
        return restaurant.toString();
    }
}
