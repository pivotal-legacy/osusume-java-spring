package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.restaurant.Restaurant;

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
                "created-date",
                1
        );
    }

    public static Restaurant newRestaurant(int id, String name) {
        return new Restaurant(
                id,
                name,
                "Roppongi",
                false,
                true,
                false,
                "",
                "created-date",
                1
        );
    }

    public static Restaurant newRestaurant(int id, String name, String created_at) {
        return new Restaurant(
                id,
                name,
                "Roppongi",
                false,
                true,
                false,
                "",
                created_at,
                1
        );
    }
}
