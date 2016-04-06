package com.tokyo.beach.application.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseRestaurantRepository implements RestaurantRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseRestaurantRepository(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Restaurant> getAll() {
        return jdbcTemplate
                .query("SELECT * FROM restaurant", (rs, rowNum) -> {
                    return new Restaurant(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getBoolean("offers_english_menu"),
                            rs.getBoolean("walk_ins_ok"),
                            rs.getBoolean("accepts_credit_cards"),
                            rs.getString("notes")
                    );
                });
    }

    @Override
    public Optional<Restaurant> get(int id) {
        List<Restaurant> restaurants = jdbcTemplate
                .query("SELECT * FROM restaurant WHERE id = ?",
                        (rs, rowNum) -> {
                            return new Restaurant(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("address"),
                                    rs.getBoolean("offers_english_menu"),
                                    rs.getBoolean("walk_ins_ok"),
                                    rs.getBoolean("accepts_credit_cards"),
                                    rs.getString("notes")
                            );
                        },
                        id
                );

        if (restaurants.size() == 1) {
            return Optional.of(restaurants.get(0));
        }

        return Optional.empty();
    }

    @Override
    public Restaurant createRestaurant(NewRestaurant newRestaurant) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "RETURNING id, name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes",
                (rs, rowNum) -> {
                    return new Restaurant(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getBoolean("offers_english_menu"),
                            rs.getBoolean("walk_ins_ok"),
                            rs.getBoolean("accepts_credit_cards"),
                            rs.getString("notes")
                    );
                },
                newRestaurant.getName(),
                newRestaurant.getAddress(),
                newRestaurant.getOffersEnglishMenu(),
                newRestaurant.getWalkInsOk(),
                newRestaurant.getAcceptsCreditCards(),
                newRestaurant.getNotes()
        );
    }
}
