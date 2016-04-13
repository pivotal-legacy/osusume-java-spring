package com.tokyo.beach.application.like;

import java.util.List;

public interface LikeRepository {
    void create(long restaurantId, long userId);
    List<Long> getLikesByUser(long userId);
}
