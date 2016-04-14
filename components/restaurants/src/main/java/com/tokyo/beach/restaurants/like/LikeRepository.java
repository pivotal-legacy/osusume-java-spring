package com.tokyo.beach.restaurants.like;

import java.util.List;

public interface LikeRepository {
    Like create(long userId, long restaurantId);
    List<Like> findForRestaurant(long restaurantId);
    List<Long> getLikesByUser(long userId);
}
