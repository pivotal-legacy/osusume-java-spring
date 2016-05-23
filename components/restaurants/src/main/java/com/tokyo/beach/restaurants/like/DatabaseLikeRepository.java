package com.tokyo.beach.restaurants.like;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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

        try {
            insertLike.execute(params);
        } catch (DuplicateKeyException dke) {
            // Duplicate entries are not allowed at the DB level.
        }

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

    @Override
    public List<Like> findForRestaurants(List<Restaurant> restaurants) {
        List<Long> restaurantIds = restaurants.stream().map(Restaurant::getId).collect(toList());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", restaurantIds);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM likes WHERE restaurant_id IN (:ids)",
                parameters,
                (rs, rowNum) -> {
                    return new Like(
                            rs.getLong("user_id"),
                            rs.getLong("restaurant_id")
                    );
                }
        );
    }
}
