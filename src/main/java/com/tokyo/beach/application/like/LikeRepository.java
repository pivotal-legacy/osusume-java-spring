package com.tokyo.beach.application.like;

import java.util.List;

public interface LikeRepository {
    Like create(long restaurantId, long userId);
    List<Long> getLikesByUser(long userId);
}
