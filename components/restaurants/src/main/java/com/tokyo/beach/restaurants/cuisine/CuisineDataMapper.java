package com.tokyo.beach.restaurants.cuisine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.restaurants.cuisine.CuisineRowMapper.cuisineRowMapper;

@Repository
public class CuisineDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CuisineDataMapper(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Cuisine> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM cuisine",
                cuisineRowMapper
        );
    }

    public Optional<Cuisine> getCuisine(long id) {
        List<Cuisine> cuisines = jdbcTemplate.query("SELECT * FROM cuisine where id = ?",
                cuisineRowMapper,
                id
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
                cuisineRowMapper
        );
    }

    public Cuisine findForRestaurant(long restaurantId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM cuisine WHERE id = (SELECT cuisine_id FROM restaurant WHERE id = ?)",
                cuisineRowMapper,
                restaurantId
        );
    }


}
