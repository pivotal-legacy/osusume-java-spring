package com.tokyo.beach.application.restaurant;

import com.tokyo.beach.application.photos.NewPhotoUrl;
import com.tokyo.beach.application.photos.PhotoUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Repository
public class DatabaseRestaurantRepository implements RestaurantRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseRestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Restaurant> getAll() {
         List<Restaurant> restaurants = jdbcTemplate.query("SELECT * FROM restaurant", (rs, rowNum) -> {
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
        });
        for(Restaurant restaurant: restaurants) {
            List<PhotoUrl> urls = jdbcTemplate.query("SELECT * FROM photo_url where restaurant_id = ?",
                    new Object[]{restaurant.getId()},(rs, rowNum) -> {
                        return new PhotoUrl(
                                rs.getInt("id"),
                                rs.getString("url"),
                                restaurant.getId()
                        );
                    });
            restaurant.setPhotoUrlList(urls);
        }
        return restaurants;
    }

    @Override
    public Restaurant createRestaurant(NewRestaurant newRestaurant) {
        Object[] args = newRestaurant.getParameter();
        int[] types = newRestaurant.getTypes();

        Restaurant restaurant = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "RETURNING id, name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes",
                args, types,
                new RowMapper<Restaurant>() {
            public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Restaurant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getBoolean("offers_english_menu"),
                        rs.getBoolean("walk_ins_ok"),
                        rs.getBoolean("accepts_credit_cards"),
                        rs.getString("notes"),
                        new ArrayList<PhotoUrl>()
                );
            }
        });

        ArrayList<PhotoUrl> urls = new ArrayList<PhotoUrl>();
        for(NewPhotoUrl photoUrl: newRestaurant.getPhotoUrls()) {
            PhotoUrl url = jdbcTemplate.queryForObject(
                    "INSERT INTO photo_url (url, restaurant_id) " +
                            "VALUES (?, ?) " +
                            "RETURNING *",
                    new Object[]{ photoUrl.getUrl(), restaurant.getId() },
                    new int[]{ Types.VARCHAR, Types.INTEGER },
                    new RowMapper<PhotoUrl>() {
                        @Override
                        public PhotoUrl mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new PhotoUrl(
                                    rs.getInt("id"),
                                    rs.getString("url"),
                                    rs.getInt("restaurant_id")
                            );
                        }
                    }
            );
            urls.add(url);
        }
        restaurant.setPhotoUrlList(urls);
        return restaurant;

    }
}
