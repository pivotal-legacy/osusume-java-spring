package com.tokyo.beach.application.cuisine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DatabaseCuisineRepository implements CuisineRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseCuisineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Cuisine> getAll() {
        return jdbcTemplate.query("SELECT * FROM cuisine", (rs, rowNum) -> {
            return new Cuisine(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        });
    }
}
