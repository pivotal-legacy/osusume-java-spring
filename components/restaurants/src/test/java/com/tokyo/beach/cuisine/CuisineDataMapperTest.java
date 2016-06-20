package com.tokyo.beach.cuisine;

import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.NewUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CuisineDataMapperTest {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());
    private CuisineDataMapper cuisineDataMapper;

    @Before
    public void setUp() throws Exception {
        cuisineDataMapper = new CuisineDataMapper(jdbcTemplate);
        createDefaultCuisine(jdbcTemplate);
        createDefaultPriceRange(jdbcTemplate);
    }

    @After
    public void tearDown() {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void testGetAll() {
        Long cuisine1Id = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine " +
                        "(name)" +
                        "VALUES ('Test Cuisine1')" +
                        "RETURNING *",
                (rs, rowNum) -> rs.getLong("id")
        );
        Long cuisine2Id = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine " +
                        "(name)" +
                        "VALUES ('Test Cuisine2')" +
                        "RETURNING *",
                (rs, rowNum) -> rs.getLong("id")
        );

        List<Cuisine> cuisines = cuisineDataMapper.getAll();
        Cuisine exceptCuisine = new Cuisine(0L, "Not Specified");
        Cuisine expectedCuisine1 = new Cuisine(cuisine1Id, "Test Cuisine1");
        Cuisine expectedCuisine2 = new Cuisine(cuisine2Id, "Test Cuisine2");

        assertTrue(cuisines.contains(exceptCuisine));
        assertTrue(cuisines.contains(expectedCuisine1));
        assertTrue(cuisines.contains(expectedCuisine2));
    }

    @Test
    public void testGetCuisine() {
        Long cuisineId = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine " +
                        "(name) " +
                        "VALUES ('Cuisine Test1') " +
                        "RETURNING id",
                (rs, rowNum) -> rs.getLong("id")
        );

        Cuisine cuisine = cuisineDataMapper.getCuisine(String.valueOf(cuisineId)).orElse(null);
        Cuisine expectedCuisine = new Cuisine(cuisineId, "Cuisine Test1");

        assertThat(cuisine, is(expectedCuisine));
    }

    @Test
    public void testGetCuisine_withInvalidId() {
        Optional<Cuisine> maybeCuisine = cuisineDataMapper.getCuisine("1");

        assertFalse(maybeCuisine.isPresent());
    }

    @Test
    public void testCreateCuisine() {
        NewCuisine newCuisine = new NewCuisine("Test Cuisine");

        cuisineDataMapper.createCuisine(newCuisine);

        Cuisine actualCuisine = jdbcTemplate.queryForObject("SELECT * FROM cuisine WHERE name=?",
                new Object[]{"Test Cuisine"},
                (rs, rowNum) -> {
                    return new Cuisine(rs.getLong("id"), rs.getString("name"));
                }
        );

        assertThat(actualCuisine.getName(), is("Test Cuisine"));
    }

    @Test
    public void test_findForRestaurant_returnCuisine() {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("joe@pivotal.io", "password", "Joe")
        ).getId();

        Long cuisineId = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine (name) VALUES " +
                        "('Cuisine Test1') RETURNING id",
                (rs, rowNum) -> rs.getLong("id")
        );
        Restaurant restaurant = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name, cuisine_id, created_by_user_id) VALUES " +
                        "('TEST RESTAURANT', ?, ?) RETURNING *",
                (rs, rowNum) -> {
                    return new Restaurant(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("notes"),
                            rs.getString("created_at"),
                            rs.getString("updated_at"),
                            rs.getLong("created_by_user_id"),
                            0L,
                            rs.getLong("cuisine_id")
                    );
                },
                cuisineId,
                userId
        );

        Cuisine cuisine = cuisineDataMapper.findForRestaurant(restaurant.getId());

        assertThat(cuisine.getId(), is(cuisineId));
        assertThat(cuisine.getName(), is("Cuisine Test1"));
    }

    @Test
    public void test_findForRestaurant_returnNotSpecified_whenCuisineTypeNotSpecified() {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new NewUser("joe@pivotal.io", "password", "Joe")
        ).getId();

        Restaurant restaurant = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name, created_by_user_id) VALUES " +
                        "('TEST RESTAURANT', ?) RETURNING *",
                (rs, rowNum) -> {
                    return new Restaurant(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("notes"),
                            rs.getString("created_at"),
                            rs.getString("updated_at"),
                            rs.getLong("created_by_user_id"),
                            0L,
                            rs.getLong("cuisine_id")
                    );
                },
                userId
        );

        Cuisine cuisine = cuisineDataMapper.findForRestaurant(restaurant.getId());

        assertThat(cuisine.getId(), is(0L));
        assertThat(cuisine.getName(), is("Not Specified"));
    }
}
