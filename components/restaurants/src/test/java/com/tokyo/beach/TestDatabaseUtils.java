package com.tokyo.beach;

import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.comment.NewComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantDataMapper;
import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.restaurants.user.User;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static com.tokyo.beach.restaurants.comment.CommentRowMapper.commentRowMapper;
import static com.tokyo.beach.restaurants.photos.PhotoUrlRowMapper.photoUrlRowMapper;
import static com.tokyo.beach.restaurants.restaurant.RestaurantRowMapper.restaurantRowMapper;

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

    public static User insertUserIntoDatabase(
            JdbcTemplate jdbcTemplate,
            NewUser newUser
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password", "name")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", newUser.getEmail());
        params.put("password", newUser.getPassword());
        params.put("name", newUser.getName());

        long id = insert.executeAndReturnKey(params).longValue();
        return new User(id, newUser.getEmail(), newUser.getName());
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
                .usingColumns("name", "address", "notes",
                        "cuisine_id", "created_by_user_id", "price_range_id")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("name", newRestaurant.getName());
        params.put("address", newRestaurant.getAddress());
        params.put("notes", newRestaurant.getNotes());
        params.put("cuisine_id", newRestaurant.getCuisineId());
        params.put("created_by_user_id", userId);
        params.put("price_range_id", newRestaurant.getPriceRangeId());

        long id = insert.executeAndReturnKey(params).longValue();
        return jdbcTemplate.queryForObject(
                "SELECT * from restaurant where id = ?",
                restaurantRowMapper,
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

    public static PhotoUrl insertPhotoUrlIntoDatabase(
            JdbcTemplate jdbcTemplate,
            PhotoUrl photoUrl
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("photo_url")
                .usingColumns("url", "restaurant_id")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("url", photoUrl.getUrl());
        params.put("restaurant_id", photoUrl.getRestaurantId());

        long id = insert.executeAndReturnKey(params).longValue();

        return jdbcTemplate.queryForObject(
                "SELECT * FROM photo_url WHERE id = ?",
                photoUrlRowMapper,
                id
        );
    }

    public static Comment insertCommentIntoDatabase(
            JdbcTemplate jdbcTemplate,
            NewComment newComment,
            long createdByUserId,
            long restaurantId
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("comment")
                .usingColumns("content", "restaurant_id", "created_by_user_id")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("content", newComment.getComment());
        params.put("restaurant_id", restaurantId);
        params.put("created_by_user_id", createdByUserId);

        long id = insert.executeAndReturnKey(params).longValue();

        return jdbcTemplate.queryForObject(
                "SELECT * FROM comment WHERE id = ?",
                commentRowMapper,
                id
        );
    }

    public static void insertSessionIntoDatabase(
            JdbcTemplate jdbcTemplate,
            String token,
            long userId
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("session")
                .usingColumns("token", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("user_id", userId);

        insert.execute(params);
    }

    public static void truncateAllTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("TRUNCATE TABLE photo_url, restaurant, cuisine, session, users, comment, likes, price_range");
    }
}
