package com.tokyo.beach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseDetailedRestaurantRepository implements DetailedRestaurantRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseDetailedRestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Restaurant getRestaurant(String id) {
        Restaurant restaurant = jdbcTemplate.queryForObject("SELECT * FROM restaurant WHERE id = ?", new Object[]{id},
                new int[]{Types.INTEGER},
                        (rs, rowNum) -> {
                    return new Restaurant(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getBoolean("offers_english_menu"),
                            rs.getBoolean("walk_ins_ok"),
                            rs.getBoolean("accepts_credit_cards"),
                            rs.getString("notes"),
                            new ArrayList()
                    );
                }
        );

        List<PhotoUrl> photoUrls = jdbcTemplate.query("SELECT * FROM photo_url WHERE restaurant_id = ?",
                    new Object[]{ restaurant.getId() },
                    (rs, rowNum) -> {
                        return new PhotoUrl(
                                rs.getInt("id"),
                                rs.getString("url"),
                                rs.getInt("restaurant_id")
                        );
                    }
        );

        if (photoUrls != null) restaurant.setPhotoUrlList(photoUrls);

        return restaurant;
    }
}
