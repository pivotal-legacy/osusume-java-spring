package com.tokyo.beach.restaurants.pricerange;

import com.tokyo.beach.restaurants.restaurant.Restaurant;

import java.util.List;
import java.util.Optional;

public interface PriceRangeRepository {
    List<PriceRange> getAll();
    Optional<PriceRange> get(Long id);
    PriceRange findForRestaurant(Restaurant restaurant);
}
