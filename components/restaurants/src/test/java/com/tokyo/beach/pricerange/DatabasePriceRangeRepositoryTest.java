package com.tokyo.beach.pricerange;

import com.tokyo.beach.restaurant.RestaurantFixtures;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import com.tokyo.beach.restaurants.pricerange.DatabasePriceRangeRepository;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeRepository;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class DatabasePriceRangeRepositoryTest {
    private PriceRangeRepository priceRangeRepository;
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());

    @Before
    public void setUp() throws Exception {
        priceRangeRepository = new DatabasePriceRangeRepository(jdbcTemplate);
    }

    @After
    public void tearDown() {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_getAll_returnsPriceRanges() throws Exception {
        Long priceRangeId1 = insertPriceRangeIntoDatabase(
                jdbcTemplate,
                "Price Range #1"
        );
        Long priceRangeId2 = insertPriceRangeIntoDatabase(
                jdbcTemplate,
                "Price Range #2"
        );


        List<PriceRange> actualPriceRanges = priceRangeRepository.getAll();


        List<PriceRange> expectedPriceRanges = Arrays.asList(
                new PriceRange(priceRangeId1, "Price Range #1"),
                new PriceRange(priceRangeId2, "Price Range #2")
        );

        assertEquals(expectedPriceRanges, actualPriceRanges);
    }

    @Test
    public void test_get_returnsPriceRange() throws Exception {
        Long priceRangeId = insertPriceRangeIntoDatabase(
                jdbcTemplate,
                "Price Range #1"
        );

        PriceRange actualPriceRange = priceRangeRepository.getPriceRange(priceRangeId).get();

        assertEquals(new PriceRange(priceRangeId, "Price Range #1"), actualPriceRange);
    }

    @Test
    public void test_findForRestaurant_findsPriceRangeForRestaurant() throws Exception {
        Number userId = insertUserIntoDatabase(
                jdbcTemplate,
                new UserRegistration("user_email", "password", "username")
        );
        Long cuisineId = insertCuisineIntoDatabase(
                jdbcTemplate,
                new NewCuisine("cuisine_name")
        );
        Long priceRangeId = insertPriceRangeIntoDatabase(
                jdbcTemplate,
                "Price Range #1"
        );
        Long insertedRestaurantId = insertRestaurantIntoDatabase(
                jdbcTemplate,
                new NewRestaurant(
                        "restaurant_name",
                        "address",
                        true,
                        true,
                        true,
                        "",
                        cuisineId,
                        priceRangeId,
                        emptyList()
                ),
                userId.longValue()
        );

        PriceRange actualPriceRange = priceRangeRepository.findForRestaurant(
                RestaurantFixtures.newRestaurant(insertedRestaurantId.intValue())
        );

        assertEquals(new PriceRange(priceRangeId, "Price Range #1"), actualPriceRange);
    }
}
