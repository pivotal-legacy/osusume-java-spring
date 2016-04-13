package com.tokyo.beach.like;

import com.tokyo.beach.application.like.DatabaseLikeRepository;
import com.tokyo.beach.application.like.Like;
import com.tokyo.beach.application.restaurant.NewRestaurant;
import com.tokyo.beach.application.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DatabaseLikeRepositoryTest {
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_create_persistsToLikesTable() throws Exception {
        createDefaultCuisine(jdbcTemplate);

        long createdByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("hiro@gmail.com", "password", "Hiro")
        );

        long restaurantId = insertRestaurantIntoDatabase(jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        0L,
                        emptyList()),
                createdByUserId
        );

        long likeByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("yuki@gmail.com", "password", "Yuki")
        );


        DatabaseLikeRepository likeRepository = new DatabaseLikeRepository(jdbcTemplate);
        Like createdLike = likeRepository.create(restaurantId, likeByUserId);


        String sql = "SELECT id FROM likes WHERE restaurant_id = ? and user_id = ?";
        long persistedLikeId = jdbcTemplate.queryForObject(
                sql, new Object[] { restaurantId, likeByUserId }, Integer.class
        );
        assertEquals(createdLike.getId(), persistedLikeId);
    }

    @Test
    public void test_getLikesByUser_returnsRestaurantIdsList() {
        createDefaultCuisine(jdbcTemplate);

        long createdByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("hiro@gmail.com", "password", "Hiro")
        );

        long restaurantId = insertRestaurantIntoDatabase(jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        0L,
                        emptyList()),
                createdByUserId
        );

        long likeByUserId = insertUserIntoDatabase(jdbcTemplate,
                new UserRegistration("yuki@gmail.com", "password", "Yuki")
        );
        SimpleJdbcInsert insertLike = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes")
                .usingColumns("restaurant_id", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("restaurant_id", restaurantId);
        params.put("user_id", likeByUserId);

        insertLike.execute(params);


        DatabaseLikeRepository likeRepository = new DatabaseLikeRepository(jdbcTemplate);
        List<Long> ids = likeRepository.getLikesByUser(likeByUserId);


        assertThat(ids.size(), is(1));
        assertThat(ids.get(0), is(restaurantId));
    }

}
