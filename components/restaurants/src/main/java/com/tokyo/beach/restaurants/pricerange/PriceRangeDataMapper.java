package com.tokyo.beach.restaurants.pricerange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PriceRangeDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PriceRangeDataMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PriceRange> getAll() {
        return jdbcTemplate.query(
                "SELECT * FROM price_range",
                PriceRangeDataMapper::mapRow
        );
    }

    public Optional<PriceRange> getPriceRange(Long id) {
        List<PriceRange> priceRanges = jdbcTemplate.query(
                "SELECT * FROM price_range WHERE id = ?",
                PriceRangeDataMapper::mapRow,
                id
        );

        if (priceRanges.size() == 1) {
            return Optional.of(priceRanges.get(0));
        }

        return Optional.empty();
    }

    public PriceRange findForRestaurant(long restaurantId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM price_range WHERE id = " +
                        "(SELECT price_range_id FROM restaurant WHERE id = ?)",
                PriceRangeDataMapper::mapRow,
                restaurantId
        );
    }

    private static PriceRange mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PriceRange(
                rs.getLong("id"),
                rs.getString("range")
        );
    }
}
