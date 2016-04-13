package com.tokyo.beach.application.like;

public interface LikeRepository {
    Like create(long restaurantId, long userId);
}
