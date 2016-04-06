package com.tokyo.beach.application.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DatabaseUserRepository implements UserRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DatabaseUser create(String email, String password, String name) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password", "name")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);

        Number id = insert.executeAndReturnKey(params);

        return new DatabaseUser(id.longValue(), email, name);
    }

    @Override
    public Optional<DatabaseUser> get(LogonCredentials credentials) {
        String sql = "SELECT id, email, name FROM users WHERE lower(email) = ? AND password = ?";
        List<DatabaseUser> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return new DatabaseUser(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("name")
                    );
                },
                credentials.getEmail().toLowerCase(),
                credentials.getPassword()
        );

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        }

        return Optional.empty();
    }
}
