package com.tokyo.beach.application.like;

public interface LikeRepository {
    void create(long restaurantId, long userId);
}
