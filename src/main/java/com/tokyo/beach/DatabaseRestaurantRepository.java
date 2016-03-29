package com.tokyo.beach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "INSERT INTO restaurant (name) VALUES ('" + newRestaurant.getName() + "') RETURNING id, name";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Restaurant>() {
            public Restaurant mapRow(ResultSet result, int rowNum) throws SQLException {
                String name = result.getString("name");
                int id = result.getInt("id");
                Restaurant restaurant = new Restaurant(id, name);
                return restaurant;
            }
        });
    }
}
