package com.tokyo.beach.restaurants.cuisine;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CuisineRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CuisineRepository(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Cuisine> getAll() {
        return jdbcTemplate.query("SELECT * FROM cuisine", (rs, rowNum) -> {
            return new Cuisine(
                    rs.getLong("id"),
                    rs.getString("name")
            );
        });
    }

    public Optional<Cuisine> getCuisine(String id) {
        List<Cuisine> cuisines = jdbcTemplate.query("SELECT * FROM cuisine where id = ?",
                new Object[]{id}, new int[]{Types.BIGINT},
                (rs, rowNum) -> {
                    return new Cuisine(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                }
        );

        if ( cuisines.size() != 1 ) {
            return Optional.empty();
        }
        else {
            return Optional.of(cuisines.get(0));
        }
    }

    public Cuisine createCuisine(NewCuisine newCuisine) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO cuisine (name) VALUES (?) RETURNING *",
                new Object[]{newCuisine.getName()}, new int[]{Types.VARCHAR},
                (rs, rowNum) -> {
                    return new Cuisine(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                }
        );
    }

    public Cuisine findForRestaurant(Restaurant restaurant) {
        List<Cuisine> cuisines = jdbcTemplate.query(
                "SELECT * FROM cuisine WHERE id = " +
                        "(SELECT cuisine_id FROM restaurant WHERE id = ?)",
                (rs, rowNum) -> {
                    return new Cuisine(
                            rs.getLong("id"),
                            rs.getString("name")
                    );
                },
                restaurant.getId()
        );
        if (cuisines.size() < 1) {
            return getCuisine("0").orElse(null);
        }
        return cuisines.get(0);
    }

}
