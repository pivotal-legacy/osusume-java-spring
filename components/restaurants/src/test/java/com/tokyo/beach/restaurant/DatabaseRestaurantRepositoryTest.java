package com.tokyo.beach.restaurant;

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
                .persist(jdbcTemplate);

        Restaurant restaurant2 = new RestaurantFixture()
                .withName("Butagumi")
                .withUser(user)
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
                "INSERT INTO restaurant (name, created_by_user_id) " +
                        "VALUES ('Amazing Restaurant', ?) " +
                        "RETURNING id",
                (rs, rowNum) -> {
                    return rs.getLong("id");
                },
                userId
        );


        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(id);


        assertThat(maybeRestaurant.get().getName(), is("Amazing Restaurant"));
    }

    @Test
    public void test_get_returnsEmptyOptionalForInvalidRestaurantId() throws Exception {
        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(999);


        assertFalse(maybeRestaurant.isPresent());
    }

    @Test
    public void test_getRestaurantsPostedByUser() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("user_email", "password", "username")
        ).getId();
        Long cuisineId = insertCuisineIntoDatabase(
                jdbcTemplate,
                new NewCuisine("cuisine_name")
        ).getId();
        Long restaurantId = insertRestaurantIntoDatabase(
                jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        cuisineId,
                        0L,
                        emptyList()
                ),
                userId.longValue()
        ).getId();

        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsPostedByUser(userId.longValue());

        assertEquals(restaurantList.size(), 1);
        assertThat(restaurantList.get(0).getName(), is("restaurant_name"));
        assertThat(restaurantList.get(0).getCreatedByUserId(), is(userId.longValue()));
    }

    @Test
    public void test_getRestaurantByIds_returnsListRestaurants() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("user_email", "password", "username")
        ).getId();
        Long cuisineId = insertCuisineIntoDatabase(
                jdbcTemplate,
                new NewCuisine("cuisine_name")
        ).getId();
        Long restaurantId = insertRestaurantIntoDatabase(
                jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        cuisineId,
                        0L,
                        emptyList()
                ),
                userId.longValue()
        ).getId();

        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsByIds(singletonList(restaurantId));

        assertEquals(restaurantList.size(), 1);
        assertThat(restaurantList.get(0).getId(), is(restaurantId));
        assertThat(restaurantList.get(0).getName(), is("restaurant_name"));

    }

    @Test
    public void test_updateRestaurant_updatesRestaurant() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("rob@pivotal.io", "password", "Rob")
        ).getId();

        NewRestaurant newRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Healthy!",
                0L,
                0L,
                emptyList()
        );

        Long restaurantId = insertRestaurantIntoDatabase(
                jdbcTemplate,
                newRestaurant,
                userId.longValue()
        ).getId();

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
                restaurantId,
                updatedNewRestaurant
        );


        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                restaurantId
        );

        assertEquals(map.get("id"), updatedRestaurant.getId());
        assertEquals(map.get("name"), updatedRestaurant.getName());
        assertEquals(map.get("address"), updatedRestaurant.getAddress());
        assertEquals(map.get("offers_english_menu"), updatedRestaurant.getOffersEnglishMenu());
        assertEquals(map.get("walk_ins_ok"), updatedRestaurant.getWalkInsOk());
        assertEquals(map.get("accepts_credit_cards"), updatedRestaurant.getAcceptsCreditCards());
        assertEquals(map.get("notes"), updatedRestaurant.getNotes());
        assertEquals(map.get("created_by_user_id"), userId.longValue());
        assertEquals(map.get("cuisine_id"), 0L);
    }
}
