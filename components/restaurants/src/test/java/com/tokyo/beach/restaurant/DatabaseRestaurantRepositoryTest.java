package com.tokyo.beach.restaurant;

import com.tokyo.beach.cuisine.CuisineFixture;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import com.tokyo.beach.restaurants.restaurant.DatabaseRestaurantRepository;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.NewUser;
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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DatabaseRestaurantRepositoryTest {
    private DatabaseRestaurantRepository restaurantRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        restaurantRepository = new DatabaseRestaurantRepository(jdbcTemplate);
        createDefaultCuisine(jdbcTemplate);
        createDefaultPriceRange(jdbcTemplate);
    }

    @After
    public void tearDown() {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_getAll_returnsSortedList() {
        User user = new UserFixture()
                .withEmail("joe@pivotal.io")
                .withName("Joe")
                .persist(jdbcTemplate);

        Restaurant restaurant1 = new RestaurantFixture()
                .withName("Afuri")
                .withUser(user)
                .withUpdatedAt("2016-01-01")
                .persist(jdbcTemplate);

        Restaurant restaurant2 = new RestaurantFixture()
                .withName("Butagumi")
                .withUser(user)
                .withUpdatedAt("2016-01-02")
                .persist(jdbcTemplate);


        List<Restaurant> restaurants = restaurantRepository.getAll();


        List<Restaurant> expectedRestaurants = asList(
                new Restaurant(
                        restaurant2.getId(),
                        "Butagumi",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
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
                    "",
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
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("joe@pivotal.io", "password", "Joe")
        ).getId();

        Long priceRangeId = insertPriceRangeIntoDatabase(
                jdbcTemplate,
                "1000~1999"
        ).getId();

        Long cuisineId = insertCuisineIntoDatabase(
                jdbcTemplate,
                new NewCuisine(
                        "Fried Chicken"
                )
        ).getId();

        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                cuisineId,
                priceRangeId,
                emptyList()
        );


        Restaurant createdRestaurant = restaurantRepository.createRestaurant(kfcNewRestaurant, userId.longValue());


        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                createdRestaurant.getId()
        );

        assertEquals(createdRestaurant.getId(), map.get("id"));
        assertEquals(createdRestaurant.getName(), map.get("name"));
        assertEquals(createdRestaurant.getAddress(), map.get("address"));
        assertEquals(createdRestaurant.getOffersEnglishMenu(), map.get("offers_english_menu"));
        assertEquals(createdRestaurant.getWalkInsOk(), map.get("walk_ins_ok"));
        assertEquals(createdRestaurant.getAcceptsCreditCards(), map.get("accepts_credit_cards"));
        assertEquals(createdRestaurant.getNotes(), map.get("notes"));
        assertEquals(userId.longValue(), map.get("created_by_user_id"));
        assertEquals(cuisineId, map.get("cuisine_id"));
        assertEquals(priceRangeId, map.get("price_range_id"));
        assertEquals(createdRestaurant.getUpdatedDate(), map.get("updated_at").toString());
    }

    @Test
    public void testCreateRestaurant_withoutCuisineId() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("joe@pivotal.io", "password", "Joe")
        ).getId();

        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                null,
                0L,
                emptyList()
        );


        Restaurant createdRestaurant = restaurantRepository.createRestaurant(kfcNewRestaurant, userId.longValue());


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
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("joe@pivotal.io", "password", "Joe")
        ).getId();

        long id = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name, updated_at, created_by_user_id) " +
                        "VALUES ('Amazing Restaurant', '2016-01-01', ?) " +
                        "RETURNING id",
                (rs, rowNum) -> {
                    return rs.getLong("id");
                },
                userId
        );


        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(id);


        assertThat(maybeRestaurant.get().getName(), is("Amazing Restaurant"));
        assertThat(maybeRestaurant.get().getUpdatedDate(), is("2016-01-01 00:00:00"));
    }

    @Test
    public void test_get_returnsEmptyOptionalForInvalidRestaurantId() throws Exception {
        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(999);


        assertFalse(maybeRestaurant.isPresent());
    }

    @Test
    public void test_getRestaurantsPostedByUser() throws Exception {
        User user = new UserFixture()
                .withEmail("joe@pivotal.io")
                .withName("Joe")
                .persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture()
                .withName("Afuri")
                .withUser(user)
                .withUpdatedAt("2016-01-01 16:42:19.572569")
                .persist(jdbcTemplate);


        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsPostedByUser(user.getId());

        assertEquals(restaurantList.size(), 1);
        assertThat(restaurantList.get(0).getName(), is("Afuri"));
        assertThat(restaurantList.get(0).getCreatedByUserId(), is(user.getId()));
        assertThat(restaurantList.get(0).getUpdatedDate(), is(restaurant.getUpdatedDate()));
    }

    @Test
    public void test_getRestaurantByIds_returnsListRestaurants() throws Exception {
        User user = new UserFixture()
                .withEmail("joe@pivotal.io")
                .withName("Joe")
                .persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture()
                .withName("Afuri")
                .withUser(user)
                .withUpdatedAt("2016-01-01 16:42:19.572569")
                .persist(jdbcTemplate);


        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsByIds(singletonList(restaurant.getId()));

        assertEquals(restaurantList.size(), 1);
        assertThat(restaurantList.get(0).getId(), is(restaurant.getId()));
        assertThat(restaurantList.get(0).getName(), is("Afuri"));
        assertThat(restaurantList.get(0).getUpdatedDate(), is(restaurant.getUpdatedDate()));

    }

    @Test
    public void test_updateRestaurant_updatesRestaurant() throws Exception {
        User user = new UserFixture()
                .withEmail("rob@pivotal.io")
                .withName("Rob")
                .persist(jdbcTemplate);
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


        Restaurant updatedRestaurant = restaurantRepository.updateRestaurant(
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
