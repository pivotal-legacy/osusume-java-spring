package com.tokyo.beach;

import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
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

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void testCreateRestaurant() throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());
        try {
            DatabaseRestaurantRepository restaurantRepository = new DatabaseRestaurantRepository(jdbcTemplate);


            restaurantRepository.createRestaurant(new NewRestaurant("KFC"));


            final List<Restaurant> actualRestaurantList = new ArrayList<>();
            jdbcTemplate.query(
                    "SELECT * FROM restaurant WHERE name = 'KFC'",
                    (rs, rowNum) -> new Restaurant(rs.getInt("id"), rs.getString("name"))
            ).forEach(restaurant -> actualRestaurantList.add(restaurant));
            assertThat(actualRestaurantList.get(0).getName(), is("KFC"));
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
