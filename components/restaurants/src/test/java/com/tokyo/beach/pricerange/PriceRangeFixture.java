package com.tokyo.beach.pricerange;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import org.springframework.jdbc.core.JdbcTemplate;

public class PriceRangeFixture {
    private String range = "Price-Range Not Specified";

    public PriceRangeFixture withRange(String range) {
        this.range = range;
        return this;
    }

    public PriceRange build() {
        return new PriceRange(range);
    }

    public PriceRange persist(JdbcTemplate jdbcTemplate) {
        return TestDatabaseUtils.insertPriceRangeIntoDatabase(
                jdbcTemplate,
                range
        );
    }

}
