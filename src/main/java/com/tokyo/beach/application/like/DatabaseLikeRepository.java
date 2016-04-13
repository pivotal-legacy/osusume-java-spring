package com.tokyo.beach.application.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseLikeRepository implements LikeRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseLikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Like create(long userId, long restaurantId) {
        SimpleJdbcInsert insertLike = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes")
                .usingColumns("restaurant_id", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("restaurant_id", restaurantId);
        params.put("user_id", userId);

        insertLike.execute(params);
        return new Like(userId, restaurantId);
    }

    @Override
    public List<Like> findForRestaurant(long restaurantId) {
        return jdbcTemplate.query(
                "SELECT * FROM likes WHERE restaurant_id = ?",
                (rs, rowNum) -> {
                    return new Like(
                            rs.getLong("user_id"),
                            rs.getLong("restaurant_id")
                    );
                },
                restaurantId
        );
    }

    @Override
    public List<Long> getLikesByUser(long userId) {
        String sql = "SELECT restaurant_id FROM likes WHERE user_id = ?";
        List<Long> restaurantIds = jdbcTemplate.queryForList(
                sql,
                Long.class,
                userId
        );

        return restaurantIds;
    }
}
