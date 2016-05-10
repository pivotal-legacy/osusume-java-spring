package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;

public class NewRestaurantFixture {
    private long priceRangeId = 0;
    private long cuisineId = 0;
    private Restaurant restaurant = null;


    public NewRestaurantFixture withPriceRangeId(long priceRangeId) {
        this.priceRangeId = priceRangeId;
        return this;
    }

    public NewRestaurantFixture withCuisineId(long cuisineId) {
        this.cuisineId = cuisineId;
        return this;
    }


    public NewRestaurantFixture withRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }

    public NewRestaurant build() {
        return new NewRestaurant(
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getOffersEnglishMenu(),
                restaurant.getWalkInsOk(),
                restaurant.getAcceptsCreditCards(),
                restaurant.getNotes(),
                cuisineId,
                priceRangeId,
                null
        );
    }
}
