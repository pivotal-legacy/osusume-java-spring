package com.tokyo.beach.cuisine;

import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.cuisine.CuisineRepository;
import com.tokyo.beach.application.cuisine.DatabaseCuisineRepository;
import org.junit.After;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseCuisineRepositoryTest {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());

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

    private static DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
