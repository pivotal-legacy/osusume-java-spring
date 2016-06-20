package com.tokyo.beach.restaurants.cuisine;

import org.springframework.jdbc.core.RowMapper;

public class CuisineRowMapper {
    public static RowMapper<Cuisine> cuisineRowMapper = (rs, i) ->
            new Cuisine(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
