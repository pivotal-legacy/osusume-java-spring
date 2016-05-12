package com.tokyo.beach;

import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.DatabaseUser;
import com.tokyo.beach.restaurants.user.UserRegistration;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TestDatabaseUtils {
    public static DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }

    public static void createDefaultCuisine(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO cuisine (id, name) " +
                "SELECT 0, 'Not Specified' " +
                "WHERE NOT EXISTS (SELECT id FROM cuisine WHERE id=0)");
    }

    public static void createDefaultPriceRange(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("INSERT INTO price_range (id, range) " +
                "SELECT 0, 'Not Specified' " +
                "WHERE NOT EXISTS (SELECT id FROM price_range WHERE id=0)");
    }

    public static DatabaseUser insertUserIntoDatabase(
            JdbcTemplate jdbcTemplate,
            UserRegistration userRegistration
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password", "name")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", userRegistration.getEmail());
        params.put("password", userRegistration.getPassword());
        params.put("name", userRegistration.getName());

        long id = insert.executeAndReturnKey(params).longValue();
        return new DatabaseUser(id, userRegistration.getEmail(), userRegistration.getName());
    }

    public static Cuisine insertCuisineIntoDatabase(
            JdbcTemplate jdbcTemplate,
            NewCuisine newCuisine
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("cuisine")
                .usingColumns("name")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", newCuisine.getName());

        long id = insert.executeAndReturnKey(params).longValue();
        return new Cuisine(id, newCuisine.getName());
    }

    public static PriceRange insertPriceRangeIntoDatabase(
            JdbcTemplate jdbcTemplate,
            String priceRangeRange
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("price_range")
                .usingColumns("range")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("range", priceRangeRange);

        long id = insert.executeAndReturnKey(params).longValue();
        return new PriceRange(id, priceRangeRange);
    }

    public static Restaurant insertRestaurantIntoDatabase(
            JdbcTemplate jdbcTemplate,
            NewRestaurant newRestaurant,
            Long userId
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("restaurant")
                .usingColumns("name", "cuisine_id", "created_by_user_id", "price_range_id")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", newRestaurant.getName());
        params.put("cuisine_id", newRestaurant.getCuisineId());
        params.put("created_by_user_id", userId);
        params.put("price_range_id", newRestaurant.getPriceRangeId());

        long id = insert.executeAndReturnKey(params).longValue();
        return jdbcTemplate.queryForObject(
                "SELECT * from restaurant where id = ?",
                (rs, rowNum) -> {
                    return new Restaurant(
                            id,
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getBoolean("offers_english_menu"),
                            rs.getBoolean("walk_ins_ok"),
                            rs.getBoolean("accepts_credit_cards"),
                            rs.getString("notes"),
                            rs.getString("created_at"),
                            rs.getLong("created_by_user_id"),
                            rs.getLong("price_range_id")
                    );
                },
                id
        );
    }

    public static Like insertLikeIntoDatabase(
            JdbcTemplate jdbcTemplate,
            Like like
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes")
                .usingColumns("restaurant_id", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("restaurant_id", like.getRestaurantId());
        params.put("user_id", like.getUserId());

        insert.execute(params);

        return like;
    }

    public static void truncateAllTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("TRUNCATE TABLE photo_url, restaurant, cuisine, session, users, comment, likes, price_range");
    }
}
