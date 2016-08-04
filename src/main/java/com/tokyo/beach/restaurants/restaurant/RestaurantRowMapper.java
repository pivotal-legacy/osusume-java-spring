package com.tokyo.beach.restaurants.restaurant;

import org.springframework.jdbc.core.RowMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RestaurantRowMapper {
    public static RowMapper<Restaurant> restaurantRowMapper = (rs, i) ->
            new Restaurant(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("nearest_station"),
                rs.getString("place_id"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getString("notes"),
                ZonedDateTime.ofInstant(rs.getTimestamp("created_at").toInstant(), ZoneId.of("UTC")),
                ZonedDateTime.ofInstant(rs.getTimestamp("updated_at").toInstant(), ZoneId.of("UTC")),
                rs.getLong("created_by_user_id"),
                rs.getLong("price_range_id"),
                rs.getLong("cuisine_id")
            );
}
