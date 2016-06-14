package com.tokyo.beach.restaurants.pricerange;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public class PriceRangeRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PriceRangeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public Optional<PriceRange> getPriceRange(Long id) {
        List<PriceRange> priceRanges = jdbcTemplate.query(
                "SELECT * FROM price_range WHERE id = ?",
                (rs, rowNum) -> {
                    return new PriceRange(
                            rs.getLong("id"),
                            rs.getString("range")
                    );
                },
                id
        );

        if (priceRanges.size() == 1) {
            return Optional.of(priceRanges.get(0));
        }

        return Optional.empty();
    }

    public PriceRange findForRestaurant(Restaurant restaurant) {
        List<PriceRange> priceRanges = jdbcTemplate.query(
                "SELECT * FROM price_range WHERE id = " +
                        "(SELECT price_range_id FROM restaurant WHERE id = ?)",
                (rs, rowNum) -> {
                    return new PriceRange(
                            rs.getLong("id"),
                            rs.getString("range")
                    );
                },
                restaurant.getId()
        );

        return priceRanges.get(0);
    }
}
