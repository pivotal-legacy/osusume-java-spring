package com.tokyo.beach.application.like;

import org.springframework.stereotype.Repository;

@Repository
public class DatabaseLikeRepository implements LikeRepository {

    @Override
    public void create(long restaurantId, long userId) {

    }
}
