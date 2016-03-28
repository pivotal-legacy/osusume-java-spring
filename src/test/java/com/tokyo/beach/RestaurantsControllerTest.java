package com.tokyo.beach;

import org.junit.Test;

import static org.junit.Assert.*;

public class RestaurantsControllerTest {
    @Test
    public void testGettingAListOfRestaurants() {
        RestaurantsController restaurantsController = new RestaurantsController();

        Restaurant[] restaurantList = restaurantsController.getAll();

        assertEquals(restaurantList[0].id, 1);
        assertEquals(restaurantList[0].name, "Afuri");
    }
}
