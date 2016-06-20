package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.SerializedRestaurant;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class SerializedRestaurantTest {
    @Test
    public void test_getCreatedDate() throws Exception {
        Restaurant restaurant = new RestaurantFixture()
                .withId(1L)
                .withName("Afuri")
                .withCreatedAt("2016-04-13 16:01:21.094")
                .build();

        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant, null, null, null, null, null, false, 0L
        );

        assertEquals(serializedRestaurant.getCreatedDate(), "2016-04-13T16:01:21.094Z");
    }

    @Test
    public void test_getCuisine_returnsTheCuisine() {
        Cuisine cuisine = new Cuisine(1, "Mexican");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1L)
                .withName("Afuri")
                .build();
        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant, null, cuisine, null, null, null, false, 0L
        );

        assertEquals(serializedRestaurant.getCuisine(), cuisine);
    }
}
