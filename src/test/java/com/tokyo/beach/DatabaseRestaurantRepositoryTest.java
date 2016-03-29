package com.tokyo.beach;

import org.junit.After;
import org.junit.Before;
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
    DatabaseRestaurantRepository restaurantRepository;
    JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        restaurantRepository = new DatabaseRestaurantRepository(jdbcTemplate);
    }

    @After
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM restaurant");
    }

    @Test
    public void testGetAll() {
        Integer id = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant (name)" +
                        "VALUES ('Afuri')" +
                        "RETURNING *",
                ((rs, rowNum) -> {
                    return rs.getInt("id");
                })
        );


        List<Restaurant> restaurants = restaurantRepository.getAll();


        assertThat(restaurants, is(Collections.singletonList(new Restaurant(id, "Afuri"))));
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void testCreateRestaurant() throws Exception {
        restaurantRepository.createRestaurant(new NewRestaurant("KFC"));


        final List<Restaurant> actualRestaurantList = new ArrayList<>();
        jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE name = 'KFC'",
                (rs, rowNum) -> new Restaurant(rs.getInt("id"), rs.getString("name"))
        ).forEach(restaurant -> actualRestaurantList.add(restaurant));
        assertThat(actualRestaurantList.get(0).getName(), is("KFC"));
    }

    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
