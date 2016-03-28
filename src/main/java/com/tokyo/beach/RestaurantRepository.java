package com.tokyo.beach;

import java.util.List;

public interface RestaurantRepository {

    List<Restaurant> getAll();

    Restaurant createRestaurant(NewRestaurant restaurant);
}

