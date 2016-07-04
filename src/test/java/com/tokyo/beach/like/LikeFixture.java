package com.tokyo.beach.like;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.like.Like;
import org.springframework.jdbc.core.JdbcTemplate;

public class LikeFixture {
    private long userId = 0;
    private long restaurantId = 0;

    public LikeFixture withUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public LikeFixture withRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    private Like build() {
        return new Like(
                userId,
                restaurantId
        );
    }

    public Like persist(JdbcTemplate jdbcTemplate) {
        return TestDatabaseUtils.insertLikeIntoDatabase(jdbcTemplate, this.build());
    }
}
