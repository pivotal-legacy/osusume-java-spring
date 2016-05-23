package com.tokyo.beach.restaurants.like;

import com.tokyo.beach.restaurants.restaurant.Restaurant;

import java.util.List;

public interface LikeRepository {
    Like create(long userId, long restaurantId);
    void delete(long userId, long restaurantId);
    List<Like> findForRestaurant(long restaurantId);
    List<Long> getLikesByUser(long userId);
    List<Like> findForRestaurants(List<Restaurant> restaurants);
}
