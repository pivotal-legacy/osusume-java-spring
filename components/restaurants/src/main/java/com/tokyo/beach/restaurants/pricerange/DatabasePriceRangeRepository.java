package com.tokyo.beach.restaurants.pricerange;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
                "SELECT * FROM price_range",
                (rs, rowNum) -> {
                    return new PriceRange(
                            rs.getLong("id"),
                            rs.getString("range")
                    );
                }
        );
    }

    @Override
    public Optional<PriceRange> get(Long id) {
        return Optional.empty();
    }

    @Override
    public PriceRange findForRestaurant(Restaurant restaurant) {
        return null;
    }
}
