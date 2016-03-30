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
                "INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes)" +
                        "VALUES ('Afuri', 'Roppongi', false, true, false, '')" +
                        "RETURNING *",
                ((rs, rowNum) -> {
                    return rs.getInt("id");
                })
        );


        List<Restaurant> restaurants = restaurantRepository.getAll();


        Restaurant expectedRestaurant = new Restaurant(
                id,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                ""
        );
        assertThat(restaurants, is(Collections.singletonList(expectedRestaurant)));
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void testCreateRestaurant() throws Exception {
        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                                            "KFC",
                                            "Shibuya",
                                            Boolean.TRUE,
                                            Boolean.TRUE,
                                            Boolean.TRUE,
                                            "Notes"
                                        );


        restaurantRepository.createRestaurant(kfcNewRestaurant);


        final List<Restaurant> actualRestaurantList = new ArrayList<>();
        jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE name = 'KFC'",
                (rs, rowNum) -> new Restaurant(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("address"),
                                    rs.getBoolean("offers_english_menu"),
                                    rs.getBoolean("walk_ins_ok"),
                                    rs.getBoolean("accepts_credit_cards"),
                                    rs.getString("notes")
                                )
        ).forEach(restaurant -> actualRestaurantList.add(restaurant));
        assertThat(actualRestaurantList.get(0).getName(), is("KFC"));
    }

    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
