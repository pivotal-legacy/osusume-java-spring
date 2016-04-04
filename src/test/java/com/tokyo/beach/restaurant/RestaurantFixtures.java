package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.restaurant.Restaurant;

import static java.util.Collections.emptyList;

public class RestaurantFixtures {
    public static Restaurant newRestaurant(int id) {
        return new Restaurant(
                id,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                emptyList()
        );
    }
}
