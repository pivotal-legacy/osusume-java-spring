package com.tokyo.beach;

import com.tokyo.beach.application.user.LogonCredentials;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }

    public static Number insertUserIntoDatabase(JdbcTemplate jdbcTemplate, LogonCredentials credentials) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", credentials.getEmail());
        params.put("password", credentials.getPassword());

        return insert.executeAndReturnKey(params);
    }
}
