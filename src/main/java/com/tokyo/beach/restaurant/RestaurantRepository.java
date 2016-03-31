package com.tokyo.beach.restaurant;

import java.util.List;

public interface RestaurantRepository {

    List<Restaurant> getAll();

    Restaurant createRestaurant(NewRestaurant restaurant);
}

