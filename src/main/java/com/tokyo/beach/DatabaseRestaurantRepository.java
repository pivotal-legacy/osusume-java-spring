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

            return new Restaurant(rs.getInt("id"), rs.getString("name"));
        });
    }

    @Override
    public Restaurant createRestaurant(NewRestaurant newRestaurant) {
        Object[] args = { newRestaurant.getName() };
        int[] types = { Types.VARCHAR };

        return jdbcTemplate.queryForObject("INSERT INTO restaurant (name) " +
                                            "VALUES (?) RETURNING id, name", args, types, new RowMapper<Restaurant>() {
            public Restaurant mapRow(ResultSet result, int rowNum) throws SQLException {
                String name = result.getString("name");
                int id = result.getInt("id");
                Restaurant restaurant = new Restaurant(id, name);
                return restaurant;
            }
        });
    }
}
