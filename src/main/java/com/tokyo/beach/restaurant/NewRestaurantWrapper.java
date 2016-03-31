package com.tokyo.beach.restaurant;

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
}
