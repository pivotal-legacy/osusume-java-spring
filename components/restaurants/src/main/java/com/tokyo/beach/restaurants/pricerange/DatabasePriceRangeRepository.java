package com.tokyo.beach.restaurants.pricerange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DatabasePriceRangeRepository implements PriceRangeRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabasePriceRangeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PriceRange> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM pricerange",
                (rs, rowNum) -> {
                    return new PriceRange(
                            rs.getLong("id"),
                            rs.getString("range")
                    );
                }
        );
    }
}
