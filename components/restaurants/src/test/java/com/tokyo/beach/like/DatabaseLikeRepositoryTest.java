package com.tokyo.beach.like;

import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.like.DatabaseLikeRepository;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.user.UserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        long restaurantId = new RestaurantFixture()
                .withUser(new UserFixture()
                        .withEmail("daniel@gmail.com")
                        .persist(jdbcTemplate)
                )
                .persist(jdbcTemplate)
                .getId();

        Long likeByUserId = new UserFixture()
                        .withEmail("yuki@gmail.com")
                        .persist(jdbcTemplate)
                        .getId();


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
    public void test_create_doesNotPersistDuplicates() throws Exception {
        long restaurantId = new RestaurantFixture()
                .withUser(new UserFixture()
                        .withEmail("email@a.com")
                        .persist(jdbcTemplate)
                )
                .persist(jdbcTemplate)
                .getId();

        Long likeByUserId = new UserFixture()
                .persist(jdbcTemplate)
                .getId();

        new LikeFixture()
                .withRestaurantId(restaurantId)
                .withUserId(likeByUserId)
                .persist(jdbcTemplate);


        DatabaseLikeRepository likeRepository = new DatabaseLikeRepository(jdbcTemplate);
        Like createdLike = likeRepository.create(likeByUserId, restaurantId);


        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("restaurant_id", restaurantId);
        parameters.addValue("user_id", likeByUserId);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        String sql = "SELECT * FROM likes WHERE restaurant_id = :restaurant_id AND user_id = :user_id";
        List<Like> likes = namedTemplate.query(
                sql,
                parameters,
                (rs, rowNum) -> {
                    return new Like(
                            rs.getLong("user_id"),
                            rs.getLong("restaurant_id")
                    );
                }
        );

        assertEquals(1, likes.size());
        assertEquals(createdLike, likes.get(0));
    }

    @Test
    public void test_delete_deletesRowFromTable() throws Exception {
        long restaurantId = new RestaurantFixture()
                .withUser(new UserFixture()
                        .withEmail("daniel@gmail.com")
                        .persist(jdbcTemplate)
                )
                .persist(jdbcTemplate)
                .getId();

        Long likeByUserId = new LikeFixture()
                .withUserId(new UserFixture()
                        .withEmail("yuki@gmail.com")
                        .persist(jdbcTemplate)
                        .getId()
                )
                .withRestaurantId(restaurantId)
                .persist(jdbcTemplate)
                .getUserId();


        DatabaseLikeRepository likeRepository = new DatabaseLikeRepository(jdbcTemplate);
        likeRepository.delete(likeByUserId, restaurantId);


        String sql = "SELECT count(*) FROM likes WHERE user_id = ? and restaurant_id = ?";
        int count = this.jdbcTemplate.queryForObject(
                sql,
                new Object[] { likeByUserId, restaurantId },
                Integer.class
        );
        assertThat(count, is(0));
    }

    @Test
    public void test_findForRestaurant_returnsLikeList() throws Exception {
        long createdByUserId = insertUserIntoDatabase(jdbcTemplate,
                new NewUser("hiro@gmail.com", "password", "Hiro")
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
                new NewUser("yuki@gmail.com", "password", "Yuki")
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

    @Test
    public void test_findForRestaurants_returnsLikesList() throws Exception {
        Restaurant restaurant1 = new RestaurantFixture()
                .withName("restaurant_name1")
                .withUser(new UserFixture().withEmail("mail1").persist(jdbcTemplate))
                .persist(jdbcTemplate);
        Restaurant restaurant2 = new RestaurantFixture()
                .withName("restaurant_name2")
                .withUser(new UserFixture().withEmail("mail2").persist(jdbcTemplate))
                .persist(jdbcTemplate);
        Like like1 = new LikeFixture()
                .withRestaurantId(restaurant1.getId())
                .withUserId(restaurant1.getCreatedByUserId())
                .persist(jdbcTemplate);
        Like like2 = new LikeFixture()
                .withRestaurantId(restaurant2.getId())
                .withUserId(new UserFixture().withEmail("mail3").persist(jdbcTemplate).getId())
                .persist(jdbcTemplate);


        List<Like> likes = new DatabaseLikeRepository(jdbcTemplate)
                .findForRestaurants(asList(restaurant1, restaurant2));


        assertTrue(likes.contains(like1));
        assertTrue(likes.contains(like2));
    }

    @Test
    public void test_findForRestaurants_returnsEmptyList() throws Exception {
        Restaurant restaurant1 = new RestaurantFixture()
                .withName("restaurant_name1")
                .withUser(new UserFixture().withEmail("mail1").persist(jdbcTemplate))
                .persist(jdbcTemplate);


        List<Like> likes = new DatabaseLikeRepository(jdbcTemplate)
                .findForRestaurants(asList(restaurant1));


        assertTrue(likes.isEmpty());
    }
}
