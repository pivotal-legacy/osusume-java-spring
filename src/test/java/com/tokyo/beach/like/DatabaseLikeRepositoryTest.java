package com.tokyo.beach.like;

import com.tokyo.beach.application.like.DatabaseLikeRepository;
import com.tokyo.beach.application.restaurant.NewRestaurant;
import com.tokyo.beach.application.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
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
        likeRepository.create(restaurantId, likeByUserId);


        String sql = "SELECT count(*) FROM likes WHERE restaurant_id = ? and user_id = ?";
        int count = jdbcTemplate.queryForObject(
                sql, new Object[] { restaurantId, likeByUserId }, Integer.class
        );
        assertThat(count, is(1));
    }

}
