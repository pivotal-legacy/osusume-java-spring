package com.tokyo.beach;

import java.util.List;

public interface RestaurantRepository {

    public List<Restaurant> getAll();

    public Restaurant createRestaurant(NewRestaurant restaurant);
}

