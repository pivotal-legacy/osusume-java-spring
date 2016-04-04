package com.tokyo.beach.application.cuisine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository
public class DatabaseCuisineRepository implements CuisineRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseCuisineRepository(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
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

    @Override
    public Cuisine getCuisine(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM cuisine where id=?",
                new Object[]{id}, new int[]{Types.INTEGER},
                (rs, rowNum) -> {
                    return new Cuisine(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                });
    }

    @Override
    public Cuisine createCuisine(NewCuisine newCuisine) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO cuisine (name) VALUES (?) RETURNING *",
                new Object[]{newCuisine.getName()}, new int[]{Types.VARCHAR},
                (rs, rowNum) -> {
                    return new Cuisine(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
        );
    }
}
