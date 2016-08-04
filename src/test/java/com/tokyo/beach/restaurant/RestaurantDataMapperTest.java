package com.tokyo.beach.restaurant;

import com.tokyo.beach.comment.CommentFixture;
import com.tokyo.beach.cuisine.CuisineFixture;
import com.tokyo.beach.like.LikeFixture;
import com.tokyo.beach.photos.PhotoUrlFixture;
import com.tokyo.beach.pricerange.PriceRangeFixture;
import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantDataMapper;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.user.UserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static com.tokyo.beach.restaurants.restaurant.RestaurantRowMapper.restaurantRowMapper;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RestaurantDataMapperTest {
    private RestaurantDataMapper restaurantDataMapper;
    private JdbcTemplate jdbcTemplate;
    private User user;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        restaurantDataMapper = new RestaurantDataMapper(jdbcTemplate);
        createDefaultCuisine(jdbcTemplate);
        createDefaultPriceRange(jdbcTemplate);
        user = new UserFixture()
                .withEmail("joe@pivotal.io")
                .withName("Joe")
                .persist(jdbcTemplate);
    }

    @After
    public void tearDown() {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_getAll_returnsNewestCreatedRestaurantFirst() {
        Restaurant restaurant1 = new RestaurantFixture()
                .withUser(user)
                .persist(jdbcTemplate);
        Restaurant restaurant2 = new RestaurantFixture()
                .withUser(user)
                .persist(jdbcTemplate);

        List<Restaurant> restaurants = restaurantDataMapper.getAll();

        assertThat(restaurants.get(0).getId(), is(restaurant2.getId()));
        assertThat(restaurants.get(1).getId(), is(restaurant1.getId()));
    }

    @Test
    public void testCreateRestaurant() throws Exception {
        PriceRange priceRange = new PriceRangeFixture()
                .withRange("1000~1999")
                .persist(jdbcTemplate);

        Cuisine cuisine = new CuisineFixture()
                .withName("Fried Chicken")
                .persist(jdbcTemplate);

        NewRestaurant kfcNewRestaurant = new NewRestaurantFixture()
                .withName("KFC")
                .withAddress("Shibuya")
                .withNearestStation("Shibuya Station")
                .withPlaceId("some-place-id")
                .withLatitude(3.45)
                .withLongitude(5.67)
                .withNotes("Notes")
                .withCuisineId(cuisine.getId())
                .withPriceRangeId(priceRange.getId())
                .build();


        Restaurant createdRestaurant = restaurantDataMapper.createRestaurant(kfcNewRestaurant, user.getId());

        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                createdRestaurant.getId()
        );

        assertEquals(createdRestaurant.getId(), map.get("id"));
        assertEquals(createdRestaurant.getName(), "KFC");
        assertEquals(createdRestaurant.getAddress(), "Shibuya");
        assertEquals(createdRestaurant.getNearestStation(), "Shibuya Station");
        assertEquals(createdRestaurant.getPlaceId(), "some-place-id");
        assertThat(createdRestaurant.getLatitude(), is(3.45));
        assertThat(createdRestaurant.getLongitude(), is(5.67));
        assertEquals(createdRestaurant.getNotes(), "Notes");
        assertEquals(createdRestaurant.getCreatedByUserId(), user.getId());
        assertEquals(createdRestaurant.getCuisineId().longValue(), cuisine.getId());
        assertEquals(createdRestaurant.getPriceRangeId(), priceRange.getId());
    }

    @Test
    public void testCreateRestaurant_withoutCuisineId() throws Exception {
        NewRestaurant kfcNewRestaurant = new NewRestaurantFixture()
                .withName("KFC")
                .withAddress("Shibuya")
                .withNotes("Notes")
                .build();

        Restaurant createdRestaurant = restaurantDataMapper.createRestaurant(kfcNewRestaurant, user.getId());

        Restaurant actualRestaurant = jdbcTemplate.queryForObject(
                "SELECT * FROM restaurant WHERE id = ?",
                restaurantRowMapper,
                createdRestaurant.getId()
        );

        assertEquals("KFC", actualRestaurant.getName());
        assertEquals("Shibuya", actualRestaurant.getAddress());
        assertEquals(0L, actualRestaurant.getCuisineId().longValue());
    }

    @Test
    public void test_get_returnsRestaurant() throws Exception {
        Restaurant restaurant = new RestaurantFixture()
            .withName("Amazing Restaurant")
            .withUser(user)
            .persist(jdbcTemplate);

        Optional<Restaurant> maybeRestaurant = restaurantDataMapper.get(restaurant.getId());
        assertThat(maybeRestaurant.get().getName(), is("Amazing Restaurant"));

        assertThat(maybeRestaurant.get().getCreatedDate(), is(restaurant.getCreatedDate()));
        assertThat(maybeRestaurant.get().getUpdatedDate(), is(restaurant.getUpdatedDate()));
    }

    @Test
    public void test_get_returnsEmptyOptionalForInvalidRestaurantId() throws Exception {
        Optional<Restaurant> maybeRestaurant = restaurantDataMapper.get(999);

        assertFalse(maybeRestaurant.isPresent());
    }

    @Test
    public void test_getRestaurantsPostedByUser() throws Exception {
        Restaurant restaurant = new RestaurantFixture()
                .withName("Afuri")
                .withUser(user)
                .withUpdatedAt(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")))
                .persist(jdbcTemplate);


        List<Restaurant> restaurantList = restaurantDataMapper.getRestaurantsPostedByUser(user.getId());

        assertEquals(restaurantList.size(), 1);
        assertThat(restaurantList.get(0).getName(), is("Afuri"));
        assertThat(restaurantList.get(0).getCreatedByUserId(), is(user.getId()));
        assertThat(restaurantList.get(0).getUpdatedDate(), is(restaurant.getUpdatedDate()));
    }

    @Test
    public void test_getRestaurantByIds_returnsListRestaurants() throws Exception {
        Restaurant restaurant = new RestaurantFixture()
                .withName("Afuri")
                .withUser(user)
                .withUpdatedAt(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")))
                .persist(jdbcTemplate);


        List<Restaurant> restaurantList = restaurantDataMapper.getRestaurantsByIds(singletonList(restaurant.getId()));

        assertEquals(restaurantList.size(), 1);
        assertThat(restaurantList.get(0).getId(), is(restaurant.getId()));
        assertThat(restaurantList.get(0).getName(), is("Afuri"));
        assertThat(restaurantList.get(0).getUpdatedDate(), is(restaurant.getUpdatedDate()));

    }

    @Test
    public void test_updateRestaurant_updatesRestaurant() throws Exception {
        Restaurant restaurant = new RestaurantFixture()
                .withName("Afuri")
                .withNearestStation("Roppongi Station")
                .withCuisine(new Cuisine(0L, "Not Specified"))
                .withUser(user)
                .persist(jdbcTemplate);

        Cuisine cuisine = new CuisineFixture()
                .withName("Beer")
                .persist(jdbcTemplate);

        PriceRange priceRange = new PriceRangeFixture()
                .withRange("0-1000")
                .persist(jdbcTemplate);

        NewRestaurant updatedNewRestaurant = new NewRestaurantFixture()
                .withName("Kentucky")
                .withAddress("East Shibuya")
                .withNearestStation("Shibuya Station")
                .withPlaceId("updated-place-id")
                .withLatitude(3.45)
                .withLongitude(4.56)
                .withNotes("Actually, not really healthy...")
                .withCuisineId(cuisine.getId())
                .withPriceRangeId(priceRange.getId())
                .build();

        Restaurant updatedRestaurant = restaurantDataMapper.updateRestaurant(
                restaurant.getId(),
                updatedNewRestaurant
        );

        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                restaurant.getId()
        );

        assertEquals(map.get("name"), updatedNewRestaurant.getName());
        assertEquals(map.get("address"), updatedNewRestaurant.getAddress());
        assertEquals(map.get("nearest_station"), updatedNewRestaurant.getNearestStation());
        assertEquals(map.get("place_id"), updatedNewRestaurant.getPlaceId());
        assertThat(((BigDecimal)map.get("latitude")).doubleValue(), is(updatedNewRestaurant.getLatitude()));
        assertThat(((BigDecimal)map.get("longitude")).doubleValue(), is(updatedNewRestaurant.getLongitude()));
        assertEquals(map.get("notes"), updatedNewRestaurant.getNotes());
        assertEquals(map.get("created_by_user_id"), user.getId());
        assertEquals(map.get("cuisine_id"), updatedNewRestaurant.getCuisineId());
        assertEquals(map.get("price_range_id"), updatedNewRestaurant.getPriceRangeId());
        assertNotEquals(updatedRestaurant.getUpdatedDate(), restaurant.getUpdatedDate());
    }

    @Test
    public void test_delete_deletesRestaurant() throws Exception {
        User user = new UserFixture().persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture()
                .withUser(user)
                .persist(jdbcTemplate);
        new CommentFixture()
                .withRestaurantId(restaurant.getId())
                .withCreatedByUserId(user.getId())
                .persist(jdbcTemplate);
        new PhotoUrlFixture()
                .withUrl("test")
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);
        new LikeFixture()
                .withRestaurantId(restaurant.getId())
                .withUserId(user.getId())
                .persist(jdbcTemplate);

        restaurantDataMapper.delete(restaurant.getId());

        List<Long> restaurantIds = jdbcTemplate.queryForList(
                "SELECT id FROM restaurant WHERE id = ?",
                Long.class,
                restaurant.getId()
        );
        assertEquals(0, restaurantIds.size());

        List<Long> commentIds = jdbcTemplate.queryForList(
                "SELECT id FROM comment WHERE restaurant_id = ?",
                Long.class,
                restaurant.getId()
        );
        assertEquals(0, commentIds.size());

        List<Long> photoUrlIds = jdbcTemplate.queryForList(
                "SELECT id FROM photo_url WHERE restaurant_id = ?",
                Long.class,
                restaurant.getId()
        );
        assertEquals(0, photoUrlIds.size());

        List<Long> likeIds = jdbcTemplate.queryForList(
                "SELECT id FROM likes WHERE restaurant_id = ?",
                Long.class,
                restaurant.getId()
        );
        assertEquals(0, likeIds.size());
    }
}
