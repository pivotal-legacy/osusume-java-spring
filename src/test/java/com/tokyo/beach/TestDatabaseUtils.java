package com.tokyo.beach;

import com.tokyo.beach.application.cuisine.NewCuisine;
import com.tokyo.beach.application.restaurant.NewRestaurant;
import com.tokyo.beach.application.user.UserRegistration;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TestDatabaseUtils {
    public static DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }

    public static Long insertUserIntoDatabase(
            JdbcTemplate jdbcTemplate,
            UserRegistration userRegistration
    ) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password", "name")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", userRegistration.getEmail());
        params.put("password", userRegistration.getPassword());
        params.put("name", userRegistration.getName());

        return insert.executeAndReturnKey(params).longValue();
    }

    public static void truncateAllTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("TRUNCATE TABLE photo_url, restaurant, cuisine, session, users, comment");
    }
}
