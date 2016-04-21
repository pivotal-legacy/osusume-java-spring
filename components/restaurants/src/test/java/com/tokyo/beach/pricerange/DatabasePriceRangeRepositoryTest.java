package com.tokyo.beach.pricerange;

import com.tokyo.beach.restaurants.pricerange.DatabasePriceRangeRepository;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.*;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static org.junit.Assert.assertEquals;

public class DatabasePriceRangeRepositoryTest {
    private PriceRangeRepository priceRangeRepository;
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(buildDataSource());

    @Before
    public void setUp() throws Exception {
        priceRangeRepository = new DatabasePriceRangeRepository(jdbcTemplate);
    }

    @After
    public void tearDown() {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_getAll_returnsPriceRanges() throws Exception {
        insertPriceRange(1, "Price Range #1");
        insertPriceRange(2, "Price Range #2");


        List<PriceRange> actualPriceRanges = priceRangeRepository.getAll();


        List<PriceRange> expectedPriceRanges = Arrays.asList(
                new PriceRange(1, "Price Range #1"),
                new PriceRange(2, "Price Range #2")
        );

        assertEquals(expectedPriceRanges, actualPriceRanges);
    }

    private void insertPriceRange(long priceRangeId, String priceRangeRange) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("price_range")
                .usingColumns("id", "range");

        Map<String, Object> params = new HashMap<>();
        params.put("id", priceRangeId);
        params.put("range", priceRangeRange);

        insert.execute(params);
    }
}
