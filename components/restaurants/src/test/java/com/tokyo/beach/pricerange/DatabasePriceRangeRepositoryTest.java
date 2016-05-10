package com.tokyo.beach.pricerange;

import com.tokyo.beach.cuisine.CuisineFixture;
import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.pricerange.DatabasePriceRangeRepository;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeRepository;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.user.DatabaseUserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
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
        PriceRange persistedPriceRange1 = new PriceRangeFixture()
                .withRange("Price Range #1")
                .persist(jdbcTemplate);

        PriceRange persistedPriceRange2 = new PriceRangeFixture()
                .withRange("Price Range #2")
                .persist(jdbcTemplate);


        List<PriceRange> actualPriceRanges = priceRangeRepository.getAll();


        List<PriceRange> expectedPriceRanges = asList(
                persistedPriceRange1,
                persistedPriceRange2
        );

        assertEquals(expectedPriceRanges, actualPriceRanges);
    }

    @Test
    public void test_get_returnsPriceRange() throws Exception {
        PriceRange persistedPriceRange = new PriceRangeFixture()
                .withRange("Price Range #1")
                .persist(jdbcTemplate);

        PriceRange actualPriceRange = priceRangeRepository.getPriceRange(persistedPriceRange.getId()).get();

        assertEquals(persistedPriceRange, actualPriceRange);
    }

    @Test
    public void test_findForRestaurant_findsPriceRangeForRestaurant() throws Exception {
        PriceRange persistedPriceRange = new PriceRangeFixture().withRange("Price Range #1").persist(jdbcTemplate);
        Restaurant persistedRestaurant = new RestaurantFixture()
                .withPriceRange(persistedPriceRange)
                .withCuisine(new CuisineFixture().persist(jdbcTemplate))
                .postedByUser(new DatabaseUserFixture().withEmail("email1").persist(jdbcTemplate))
                .persist(jdbcTemplate);

        PriceRange actualPriceRange = priceRangeRepository.findForRestaurant(
                persistedRestaurant
        );

        assertEquals(persistedPriceRange, actualPriceRange);
    }

    @Test
    public void test_findForRestaurants_findsPriceRangesForRestaurantList() throws Exception {
        PriceRange priceRange = new PriceRangeFixture().withRange("Price Range #1").persist(jdbcTemplate);

        List<Long> restaurants = asList(
                new RestaurantFixture()
                        .withPriceRange(priceRange)
                        .withCuisine(new CuisineFixture().persist(jdbcTemplate))
                        .postedByUser(new DatabaseUserFixture().withEmail("email1").persist(jdbcTemplate))
                        .persist(jdbcTemplate)
                        .getId(),
                new RestaurantFixture()
                        .withPriceRange(priceRange)
                        .withCuisine(new CuisineFixture().persist(jdbcTemplate))
                        .postedByUser(new DatabaseUserFixture().withEmail("email2").persist(jdbcTemplate))
                        .persist(jdbcTemplate)
                        .getId()
        );

        List<PriceRange> actualPriceRanges = priceRangeRepository.findForRestaurants(
                asList(new RestaurantFixture().withId(restaurants.get(0)).build(),
                        new RestaurantFixture().withId(restaurants.get(1)).build()
                )
        );

        assertThat(actualPriceRanges, hasSize(restaurants.size()));
        assertEquals(actualPriceRanges.get(0).getRange(), "Price Range #1");
        assertEquals(actualPriceRanges.get(1).getRange(), "Price Range #1");
    }

    @Test
    public void test_findForRestaurants_findsDifferentPriceRangesForRestaurantList() throws Exception {
        List<Long> restaurants = asList(
                new RestaurantFixture()
                        .withPriceRange(new PriceRangeFixture().withRange("Price Range #1").persist(jdbcTemplate))
                        .withCuisine(new CuisineFixture().persist(jdbcTemplate))
                        .postedByUser(new DatabaseUserFixture().withEmail("email1").persist(jdbcTemplate))
                        .persist(jdbcTemplate)
                        .getId(),
                new RestaurantFixture()
                        .withPriceRange(new PriceRangeFixture().withRange("Price Range #2").persist(jdbcTemplate))
                        .withCuisine(new CuisineFixture().persist(jdbcTemplate))
                        .postedByUser(new DatabaseUserFixture().withEmail("email2").persist(jdbcTemplate))
                        .persist(jdbcTemplate)
                        .getId()
        );

        List<PriceRange> actualPriceRanges = priceRangeRepository.findForRestaurants(
                asList(new RestaurantFixture().withId(restaurants.get(0)).build(),
                        new RestaurantFixture().withId(restaurants.get(1)).build()
                )
        );

        assertThat(actualPriceRanges, hasSize(restaurants.size()));
        assertEquals(actualPriceRanges.get(0).getRange(), "Price Range #1");
        assertEquals(actualPriceRanges.get(1).getRange(), "Price Range #2");
    }
}
