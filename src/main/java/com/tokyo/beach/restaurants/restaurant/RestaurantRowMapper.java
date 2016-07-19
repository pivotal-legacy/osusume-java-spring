package com.tokyo.beach.restaurants.restaurant;

import org.springframework.jdbc.core.RowMapper;

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
                rs.getString("created_at"),
                rs.getString("updated_at"),
                rs.getLong("created_by_user_id"),
                rs.getLong("price_range_id"),
                rs.getLong("cuisine_id")
            );
}
