package com.tokyo.beach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DatabaseRestaurantRepository implements RestaurantRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseRestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Restaurant> getAll() {
        return jdbcTemplate.query("SELECT * FROM restaurant", (rs, rowNum) -> {
            return new Restaurant(rs.getInt("id"), rs.getString("name"));
        });
    }

    @Override
    public Restaurant createRestaurant(NewRestaurant restaurant) {
        return null;
    }
}
