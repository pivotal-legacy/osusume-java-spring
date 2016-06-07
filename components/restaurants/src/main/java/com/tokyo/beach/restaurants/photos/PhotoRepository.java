package com.tokyo.beach.restaurants.photos;

import com.tokyo.beach.restaurants.restaurant.Restaurant;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository {
    List<PhotoUrl> findForRestaurants(List<Restaurant> restaurants);
    List<PhotoUrl> createPhotosForRestaurant(long restaurantId, List<NewPhotoUrl> photos);
    List<PhotoUrl> findForRestaurant(Restaurant restaurant);
    Optional<PhotoUrl> get(long photoUrlId);
    void delete(long photoUrlId);
}
