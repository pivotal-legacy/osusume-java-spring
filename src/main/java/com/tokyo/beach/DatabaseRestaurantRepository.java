package com.tokyo.beach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
        return jdbcTemplate.query("SELECT * FROM restaurant", (rs, rowNum) -> {
            return new Restaurant(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getBoolean("offers_english_menu"),
                    rs.getBoolean("walk_ins_ok"),
                    rs.getBoolean("accepts_credit_cards"),
                    rs.getString("notes"));
        });
    }

    @Override
    public Restaurant createRestaurant(NewRestaurant newRestaurant) {
        Object[] args = newRestaurant.getParameter();
        int[] types = newRestaurant.getTypes();

        return jdbcTemplate.queryForObject("INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) " +
                                            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes", args, types, new RowMapper<Restaurant>() {
            public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Restaurant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getBoolean("offers_english_menu"),
                        rs.getBoolean("walk_ins_ok"),
                        rs.getBoolean("accepts_credit_cards"),
                        rs.getString("notes"));
            }
        });
    }
}
