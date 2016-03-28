package com.tokyo.beach;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatabaseRestaurantRepositoryTest {
    @Test
    public void testGetAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());
        try {
            Integer id = jdbcTemplate.queryForObject(
                    "INSERT INTO restaurant (name)" +
                            "VALUES ('Afuri')" +
                            "RETURNING *",
                    ((rs, rowNum) -> {
                        return rs.getInt("id");
                    })
            );
            DatabaseRestaurantRepository restaurantRepository = new DatabaseRestaurantRepository(jdbcTemplate);


            List<Restaurant> restaurants = restaurantRepository.getAll();

            assertThat(restaurants, is(Collections.singletonList(new Restaurant(id, "Afuri"))));
        } finally {
            jdbcTemplate.update("DELETE FROM restaurant");
        }
    }

    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
