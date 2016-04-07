package com.tokyo.beach.cuisine;

import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.cuisine.CuisineRepository;
import com.tokyo.beach.application.cuisine.DatabaseCuisineRepository;
import com.tokyo.beach.application.cuisine.NewCuisine;
import com.tokyo.beach.application.restaurant.Restaurant;
import com.tokyo.beach.restaurant.RestaurantFixtures;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.tokyo.beach.TestUtils.buildDataSource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseCuisineRepositoryTest {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());
    private CuisineRepository cuisineRepository;

    @Before
    public void setUp() throws Exception {
        cuisineRepository = new DatabaseCuisineRepository(jdbcTemplate);
    }

    @After
    public void tearDown() {
        jdbcTemplate.update("truncate table cuisine cascade");
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

        List<Cuisine> cuisines = cuisineRepository.getAll();
        Cuisine expectedCuisine1 = new Cuisine(cuisine1Id, "Test Cuisine1");
        Cuisine expectedCuisine2 = new Cuisine(cuisine2Id, "Test Cuisine2");

        assertThat(cuisines.get(0), is(expectedCuisine1));
        assertThat(cuisines.get(1), is(expectedCuisine2));
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

        Cuisine cuisine = cuisineRepository.getCuisine(String.valueOf(cuisineId)).get();
        Cuisine expectedCuisine = new Cuisine(cuisineId, "Cuisine Test1");

        assertThat(cuisine, is(expectedCuisine));
    }

    @Test
    public void testCreateCuisine() {
        NewCuisine newCuisine = new NewCuisine("Test Cuisine");

        cuisineRepository.createCuisine(newCuisine);

        Cuisine actualCuisine = jdbcTemplate.queryForObject("SELECT * FROM cuisine WHERE name=?",
                new Object[]{"Test Cuisine"},
                (rs, rowNum) -> {
                    return new Cuisine(rs.getLong("id"), rs.getString("name"));
                }
        );

        assertThat(actualCuisine.getName(), is("Test Cuisine"));
    }
}
