package com.tokyo.beach.restaurants.pricerange;

import org.springframework.jdbc.core.RowMapper;

public class PriceRangeRowMapper {
    public static RowMapper<PriceRange> priceRangeRowMapper = (rs, i) ->
            new PriceRange(
                    rs.getLong("id"),
                    rs.getString("range")
            );
}
