package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.restaurant.DatabaseRestaurantRepository;
import com.tokyo.beach.application.restaurant.NewRestaurant;
import com.tokyo.beach.application.restaurant.Restaurant;
import com.tokyo.beach.application.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.TestDatabaseUtils.insertUserIntoDatabase;
import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class DatabaseRestaurantRepositoryTest {
    private DatabaseRestaurantRepository restaurantRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        restaurantRepository = new DatabaseRestaurantRepository(jdbcTemplate);
        jdbcTemplate.update("insert into cuisine (id, name) " +
                "select 0, 'Not Specified' " +
                "WHERE NOT EXISTS (SELECT id From cuisine WHERE id=0)");
    }

    @After
    public void tearDown() {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_getAll() {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new UserRegistration("joe@pivotal.io", "password", "Joe")
        );

        Integer restaurantId = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant " +
                        "(name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes, created_by_user_id)" +
                        "VALUES ('Afuri', 'Roppongi', FALSE, TRUE, FALSE, '', ?)" +
                        "RETURNING *",
                (rs, rowNum) -> {
                    return rs.getInt("id");
                },
                userId
        );

        List<Restaurant> restaurants = restaurantRepository.getAll();


        Restaurant expectedRestaurant = new Restaurant(
                restaurantId,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                userId.longValue()
        );

        assertThat(restaurants, is(singletonList(expectedRestaurant)));
    }

    @Test
    public void testCreateRestaurant() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new UserRegistration("joe@pivotal.io", "password", "Joe")
        );

        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                0L,
                emptyList()
        );


        Restaurant createdRestaurant = restaurantRepository.createRestaurant(kfcNewRestaurant, userId.longValue());


        Map<String, Object> map = jdbcTemplate.queryForMap(
                "SELECT * FROM restaurant WHERE id = ?",
                createdRestaurant.getId()
        );


        assertEquals(map.get("id"), createdRestaurant.getId());
        assertEquals(map.get("name"), createdRestaurant.getName());
        assertEquals(map.get("address"), createdRestaurant.getAddress());
        assertEquals(map.get("offers_english_menu"), createdRestaurant.getOffersEnglishMenu());
        assertEquals(map.get("walk_ins_ok"), createdRestaurant.getWalkInsOk());
        assertEquals(map.get("accepts_credit_cards"), createdRestaurant.getAcceptsCreditCards());
        assertEquals(map.get("notes"), createdRestaurant.getNotes());
        assertEquals(map.get("created_by_user_id"), userId.longValue());
        assertEquals(map.get("cuisine_id"), 0L);
    }

    @Test
    public void testCreateRestaurant_withoutCuisineId() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new UserRegistration("joe@pivotal.io", "password", "Joe")
        );

        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                null,
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
                        emptyList()
                ),
                createdRestaurant.getId()
        );

        assertThat(actualRestaurant.getName(), is("KFC"));
        assertThat(actualRestaurant.getAddress(), is("Shibuya"));
        assertThat(actualRestaurant.getCuisineId(), is(0L));
    }

    @Test
    public void test_get_returnsRestaurant() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new UserRegistration("joe@pivotal.io", "password", "Joe")
        );

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

}
