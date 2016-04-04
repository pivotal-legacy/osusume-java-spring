package com.tokyo.beach.application.user;

import com.tokyo.beach.application.logon.LogonCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DatabaseUserRepository implements UserRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DatabaseUser create(String email, String password) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        Number id = insert.executeAndReturnKey(params);

        return new DatabaseUser(id, email);
    }

    @Override
    public DatabaseUser get(LogonCredentials credentials) {
        String sql = "SELECT id, email FROM users WHERE email = ? AND password = ?";
        List<DatabaseUser> users = jdbcTemplate.query(
                sql,
                new Object[]{credentials.getEmail(), credentials.getPassword()},
                (rs, rowNum) -> {
                    return new DatabaseUser(
                            rs.getInt("id"),
                            rs.getString("email")
                    );
                }
        );

        return users.get(0);
    }
}
