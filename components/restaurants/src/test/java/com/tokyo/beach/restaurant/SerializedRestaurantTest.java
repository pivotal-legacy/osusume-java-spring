package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.SerializedRestaurant;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializedRestaurantTest {
    @Test
    public void test_getCreatedDate() throws Exception {
        Restaurant restaurant = RestaurantFixtures.newRestaurant(
                1, "Afuri", "2016-04-13 16:01:21.094"
        );
        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant, null, null, null, null, null, false, 0L
        );

        assertEquals(serializedRestaurant.getCreatedDate(), "2016-04-13T16:01:21.094Z");
    }
}
