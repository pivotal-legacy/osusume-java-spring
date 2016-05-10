package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.restaurant.NewRestaurant;

public class NewRestaurantFixtures {
    public static NewRestaurant newNewRestaurant(Long priceRangeId, Long cuisineId) {
        return new NewRestaurant(
                "Not Specified",
                "Roppongi",
                false,
                true,
                false,
                "",
                cuisineId,
                priceRangeId,
                null
        );
    }
}
