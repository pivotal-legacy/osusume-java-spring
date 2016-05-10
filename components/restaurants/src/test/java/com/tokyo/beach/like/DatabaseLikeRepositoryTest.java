package com.tokyo.beach.like;

import com.tokyo.beach.restaurants.like.DatabaseLikeRepository;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class DatabaseLikeRepositoryTest {
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        createDefaultCuisine(jdbcTemplate);
        createDefaultPriceRange(jdbcTemplate);
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_create_persistsToLikesTable() throws Exception {
        long createdByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("hiro@gmail.com", "password", "Hiro")
        ).getId();

        long restaurantId = insertRestaurantIntoDatabase(jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        0L,
                        0L,
                        emptyList()),
                createdByUserId
        ).getId();

        Long likeByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("yuki@gmail.com", "password", "Yuki")
        ).getId();


        DatabaseLikeRepository likeRepository = new DatabaseLikeRepository(jdbcTemplate);
        Like createdLike = likeRepository.create(likeByUserId, restaurantId);


        String sql = "SELECT * FROM likes WHERE restaurant_id = ? AND user_id = ?";
        Like persistedLike = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    return new Like(
                            rs.getLong("user_id"),
                            rs.getLong("restaurant_id")
                    );
                },
                restaurantId,
                likeByUserId.longValue()
        );

        assertEquals(persistedLike, createdLike);
    }

    @Test
    public void test_findForRestaurant_returnsLikeList() throws Exception {
        long createdByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("hiro@gmail.com", "password", "Hiro")
        ).getId();

        long restaurantId = insertRestaurantIntoDatabase(jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        0L,
                        0L,
                        emptyList()),
                createdByUserId
        ).getId();

        Long likeByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("yuki@gmail.com", "password", "Yuki")
        ).getId();


        DatabaseLikeRepository likeRepository = new DatabaseLikeRepository(jdbcTemplate);
        String sql = "INSERT INTO likes (user_id, restaurant_id) VALUES (?, ?) RETURNING *";
        Like persistedLike = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    return new Like(
                            rs.getLong("user_id"),
                            rs.getLong("restaurant_id")
                    );
                },
                likeByUserId.longValue(),
                restaurantId
        );


        List<Like> likes = likeRepository.findForRestaurant(restaurantId);

        assertEquals(likes.get(0), persistedLike);
    }
}
