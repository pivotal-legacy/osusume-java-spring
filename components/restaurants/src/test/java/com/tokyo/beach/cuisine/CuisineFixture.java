package com.tokyo.beach.cuisine;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import org.springframework.jdbc.core.JdbcTemplate;

public class CuisineFixture {
    private String name = "cuisine_name";

    public CuisineFixture withName(String name) {
        this.name = name;
        return this;
    }

    public Cuisine build() {
        return new Cuisine(name);
    }

    public Cuisine persist(JdbcTemplate jdbcTemplate) {
        return TestDatabaseUtils.insertCuisineIntoDatabase(
                jdbcTemplate,
                new NewCuisine(name)
        );
    }

}
