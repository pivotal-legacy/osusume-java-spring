package com.tokyo.beach.application.restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    List<Restaurant> getAll();

    Optional<Restaurant> get(long id);

    Restaurant createRestaurant(NewRestaurant restaurant);
}

