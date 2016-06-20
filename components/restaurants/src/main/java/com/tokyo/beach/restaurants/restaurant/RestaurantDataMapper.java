package com.tokyo.beach.restaurants.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RestaurantDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public RestaurantDataMapper(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Restaurant> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM restaurant ORDER BY created_at DESC",
                RestaurantDataMapper::mapRow
        );
    }

    public Optional<Restaurant> get(long id) {
        List<Restaurant> restaurants = jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE id = ?",
                RestaurantDataMapper::mapRow,
                id
        );

        if (restaurants.size() == 1) {
            return Optional.of(restaurants.get(0));
        }

        return Optional.empty();
    }

    public Restaurant createRestaurant(NewRestaurant newRestaurant, Long createdByUserId) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (" +
                        "name, address, " +
                        "notes, cuisine_id, price_range_id, created_by_user_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "RETURNING " +
                        "id, name, address, " +
                        "notes, cuisine_id, created_by_user_id, price_range_id, created_at, updated_at",
                RestaurantDataMapper::mapRow,
                newRestaurant.getName(),
                newRestaurant.getAddress(),
                newRestaurant.getNotes(),
                newRestaurant.getCuisineId(),
                newRestaurant.getPriceRangeId(),
                createdByUserId
        );
    }

    public List<Restaurant> getRestaurantsPostedByUser(long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE created_by_user_id = ?",
                RestaurantDataMapper::mapRow,
                userId
        );
    }

    public List<Restaurant> getRestaurantsByIds(List<Long> restaurantIds) {
        MapSqlParameterSource parameters =  new MapSqlParameterSource();
        parameters.addValue("ids", restaurantIds);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM restaurant WHERE id IN (:ids)",
                parameters,
                RestaurantDataMapper::mapRow
        );
    }

    public Restaurant updateRestaurant(Long restaurantId, NewRestaurant restaurant) {
        return jdbcTemplate.queryForObject(
                "UPDATE restaurant SET " +
                        "(name, address, notes, updated_at) =" +
                        "(?, ?, ?, now()) " +
                        "WHERE id = ? " +
                        "RETURNING id, name, address, notes, cuisine_id, created_by_user_id, price_range_id, created_at, updated_at",
                RestaurantDataMapper::mapRow,
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getNotes(),
                restaurantId
        );
    }

    private static Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Restaurant(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("notes"),
                rs.getString("created_at"),
                rs.getString("updated_at"),
                rs.getLong("created_by_user_id"),
                rs.getLong("price_range_id"),
                rs.getLong("cuisine_id")
        );
    }
}
