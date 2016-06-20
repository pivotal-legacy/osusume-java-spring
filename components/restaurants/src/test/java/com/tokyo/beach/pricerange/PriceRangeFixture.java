package com.tokyo.beach.pricerange;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import org.springframework.jdbc.core.JdbcTemplate;

public class PriceRangeFixture {
    private String range = "Price-Range Not Specified";
    private long id = 0;

    public PriceRangeFixture withRange(String range) {
        this.range = range;
        return this;
    }

    public PriceRange build() {
        return new PriceRange(id, range);
    }

    public PriceRange persist(JdbcTemplate jdbcTemplate) {
        return TestDatabaseUtils.insertPriceRangeIntoDatabase(
                jdbcTemplate,
                range
        );
    }

}
