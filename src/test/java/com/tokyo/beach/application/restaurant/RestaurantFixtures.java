package com.tokyo.beach.application.restaurant;

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
