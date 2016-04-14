package com.tokyo.beach.restaurants.restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    List<Restaurant> getAll();

    Optional<Restaurant> get(long id);

    Restaurant createRestaurant(NewRestaurant restaurant, Long createdByUserId);

    List<Restaurant> getRestaurantsPostedByUser(long userId);

    List<Restaurant> getRestaurantsByIds(List<Long> restaurantIds);
}
