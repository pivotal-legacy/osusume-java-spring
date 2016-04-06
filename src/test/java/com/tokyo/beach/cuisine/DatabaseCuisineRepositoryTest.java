package com.tokyo.beach.cuisine;

import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.cuisine.CuisineRepository;
import com.tokyo.beach.application.cuisine.DatabaseCuisineRepository;
import com.tokyo.beach.application.cuisine.NewCuisine;
import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.tokyo.beach.TestUtils.buildDataSource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseCuisineRepositoryTest {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());

    @After
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM cuisine");
    }

    @Test
    public void testGetAll() {
        Integer japaneseId = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine " +
                        "(name)" +
                        "VALUES ('Japanese')" +
                        "RETURNING *",
                (rs, rowNum) -> rs.getInt("id")
        );
        Integer spanishId = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine " +
                        "(name)" +
                        "VALUES ('Spanish')" +
                        "RETURNING *",
                (rs, rowNum) -> rs.getInt("id")
        );

        CuisineRepository cuisineRepository = new DatabaseCuisineRepository(jdbcTemplate);

        List<Cuisine> cuisines = cuisineRepository.getAll();
        Cuisine expectedJapanese = new Cuisine(japaneseId, "Japanese");
        Cuisine expectedSpanish = new Cuisine(spanishId, "Spanish");

        assertThat(cuisines.get(0), is(expectedJapanese));
        assertThat(cuisines.get(1), is(expectedSpanish));
    }

    @Test
    public void testGetCuisine() {
        Integer cuisineId = jdbcTemplate.queryForObject(
                "INSERT INTO cuisine " +
                        "(name) " +
                        "VALUES ('Japanese') " +
                        "RETURNING id",
                (rs, rowNum) -> rs.getInt("id")
        );

        CuisineRepository cuisineRepository = new DatabaseCuisineRepository(jdbcTemplate);

        Cuisine cuisine = cuisineRepository.getCuisine(String.valueOf(cuisineId));
        Cuisine expectedCuisine = new Cuisine(cuisineId, "Japanese");

        assertThat(cuisine, is(expectedCuisine));
    }

    @Test
    public void testCreateCuisine() {
        NewCuisine newCuisine = new NewCuisine("Test Cuisine");

        CuisineRepository cuisineRepository = new DatabaseCuisineRepository(jdbcTemplate);

        cuisineRepository.createCuisine(newCuisine);

        Cuisine actualCuisine = jdbcTemplate.queryForObject("SELECT * FROM cuisine WHERE name=?",
                new Object[]{"Test Cuisine"},
                (rs, rowNum) -> {
                    return new Cuisine(rs.getInt("id"), rs.getString("name"));
                }
        );

        assertThat(actualCuisine.getName(), is("Test Cuisine"));
    }
}
