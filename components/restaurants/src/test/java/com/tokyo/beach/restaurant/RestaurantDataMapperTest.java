package com.tokyo.beach.restaurant;

import com.tokyo.beach.cuisine.CuisineFixture;
import com.tokyo.beach.pricerange.PriceRangeFixture;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.restaurant.RestaurantDataMapper;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.user.UserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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
    public void test_getAll_returnsSortedList() {
        Restaurant restaurant1 = new RestaurantFixture()
                .withName("Afuri")
                .withAddress("Roppongi")
                .withUser(user)
                .withNotes("notes")
                .withUpdatedAt("2016-01-01")
                .persist(jdbcTemplate);

        Restaurant restaurant2 = new RestaurantFixture()
                .withName("Butagumi")
                .withAddress("Roppongi")
                .withUser(user)
                .withNotes("notes")
                .withUpdatedAt("2016-01-02")
                .persist(jdbcTemplate);


        List<Restaurant> restaurants = restaurantDataMapper.getAll();


        List<Restaurant> expectedRestaurants = asList(
                new Restaurant(
                        restaurant2.getId(),
                        "Butagumi",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "notes",
                        restaurant2.getCreatedDate(),
                        restaurant2.getUpdatedDate(),
                        user.getId(),
                        0L,
                        0L
                ),
                new Restaurant(
                    restaurant1.getId(),
                    "Afuri",
                    "Roppongi",
                    false,
                    true,
                    false,
                    "notes",
                    restaurant1.getCreatedDate(),
                    restaurant1.getUpdatedDate(),
                    user.getId(),
                    0L,
                    0L
                )
            );

        assertThat(restaurants, is(expectedRestaurants));
    }

    @Test
    public void testCreateRestaurant() throws Exception {
        PriceRange priceRange = new PriceRangeFixture()
                .withRange("1000~1999")
                .persist(jdbcTemplate);

        Cuisine cuisine = new CuisineFixture()
                .withName("Fried Chicken")
                .persist(jdbcTemplate);

        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                TRUE,
                TRUE,
                TRUE,
                "Notes",
                cuisine.getId(),
                priceRange.getId(),
                emptyList()
        );


        Restaurant createdRestaurant = restaurantDataMapper.createRestaurant(kfcNewRestaurant, user.getId());


        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                createdRestaurant.getId()
        );

        assertEquals(createdRestaurant.getId(), map.get("id"));
        assertEquals(createdRestaurant.getName(), "KFC");
        assertEquals(createdRestaurant.getAddress(), "Shibuya");
        assertEquals(createdRestaurant.getOffersEnglishMenu(), true);
        assertEquals(createdRestaurant.getWalkInsOk(), true);
        assertEquals(createdRestaurant.getAcceptsCreditCards(), true);
        assertEquals(createdRestaurant.getNotes(), "Notes");
        assertEquals(createdRestaurant.getCreatedByUserId(), user.getId());
        assertEquals(createdRestaurant.getCuisineId().longValue(), cuisine.getId());
        assertEquals(createdRestaurant.getPriceRangeId(), priceRange.getId());
        assertEquals(createdRestaurant.getUpdatedDate(), map.get("updated_at").toString());
    }

    @Test
    public void testCreateRestaurant_withoutCuisineId() throws Exception {
        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                TRUE,
                TRUE,
                TRUE,
                "Notes",
                null,
                0L,
                emptyList()
        );


        Restaurant createdRestaurant = restaurantDataMapper.createRestaurant(kfcNewRestaurant, user.getId());


        NewRestaurant actualRestaurant = jdbcTemplate.queryForObject(
                "SELECT * FROM restaurant WHERE id = ?",
                (rs, rowNum) -> new NewRestaurant(
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getBoolean("offers_english_menu"),
                        rs.getBoolean("walk_ins_ok"),
                        rs.getBoolean("accepts_credit_cards"),
                        rs.getString("notes"),
                        rs.getLong("cuisine_id"),
                        0L,
                        emptyList()
                ),
                createdRestaurant.getId()
        );

        assertEquals("KFC", actualRestaurant.getName());
        assertEquals("Shibuya", actualRestaurant.getAddress());
        assertEquals(0L, actualRestaurant.getCuisineId().longValue());
    }

    @Test
    public void test_get_returnsRestaurant() throws Exception {
        long id = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name, updated_at, created_by_user_id) " +
                        "VALUES ('Amazing Restaurant', '2016-01-01', ?) " +
                        "RETURNING id",
                (rs, rowNum) -> {
                    return rs.getLong("id");
                },
                user.getId()
        );


        Optional<Restaurant> maybeRestaurant = restaurantDataMapper.get(id);


        assertThat(maybeRestaurant.get().getName(), is("Amazing Restaurant"));
        assertThat(maybeRestaurant.get().getUpdatedDate(), is("2016-01-01 00:00:00"));
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
                .withUpdatedAt("2016-01-01 16:42:19.572569")
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
                .withUpdatedAt("2016-01-01 16:42:19.572569")
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
                .withUser(user)
                .withUpdatedAt("2016-01-01 16:42:19.572569")
                .persist(jdbcTemplate);


        NewRestaurant updatedNewRestaurant = new NewRestaurant(
                "Kentucky",
                "East Shibuya",
                Boolean.FALSE,
                Boolean.FALSE,
                Boolean.FALSE,
                "Actually, not really healthy...",
                0L,
                0L,
                emptyList()
        );


        Restaurant updatedRestaurant = restaurantDataMapper.updateRestaurant(
                restaurant.getId(),
                updatedNewRestaurant
        );


        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                restaurant.getId()
        );

        assertEquals(map.get("id"), updatedRestaurant.getId());
        assertEquals(map.get("name"), updatedRestaurant.getName());
        assertEquals(map.get("address"), updatedRestaurant.getAddress());
        assertEquals(map.get("offers_english_menu"), updatedRestaurant.getOffersEnglishMenu());
        assertEquals(map.get("walk_ins_ok"), updatedRestaurant.getWalkInsOk());
        assertEquals(map.get("accepts_credit_cards"), updatedRestaurant.getAcceptsCreditCards());
        assertEquals(map.get("notes"), updatedRestaurant.getNotes());
        assertEquals(map.get("created_by_user_id"), user.getId());
        assertEquals(map.get("cuisine_id"), 0L);
        assertEquals(map.get("updated_at").toString(), updatedRestaurant.getUpdatedDate());
        assertNotEquals(updatedRestaurant.getUpdatedDate(), restaurant.getUpdatedDate());
    }
}
