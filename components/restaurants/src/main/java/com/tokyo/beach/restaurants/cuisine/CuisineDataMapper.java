package com.tokyo.beach.restaurants.cuisine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

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
                CuisineDataMapper::mapRow
        );
    }

    public Optional<Cuisine> getCuisine(String id) {
        List<Cuisine> cuisines = jdbcTemplate.query("SELECT * FROM cuisine where id = ?",
                new Object[]{id}, new int[]{Types.BIGINT},
                CuisineDataMapper::mapRow
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
                CuisineDataMapper::mapRow
        );
    }

    public Cuisine findForRestaurant(long restaurantId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM cuisine WHERE id = (SELECT cuisine_id FROM restaurant WHERE id = ?)",
                CuisineDataMapper::mapRow,
                restaurantId
        );
    }

    private static Cuisine mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Cuisine(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
