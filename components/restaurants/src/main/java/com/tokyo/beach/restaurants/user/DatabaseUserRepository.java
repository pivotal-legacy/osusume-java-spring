package com.tokyo.beach.restaurants.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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

    @Override
    public Optional<DatabaseUser> get(long userId) {
        String sql = "SELECT id, email, name FROM users WHERE id = ?";
        List<DatabaseUser> users = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    return new DatabaseUser(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("name")
                    );
                },
                userId
        );

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        }

        return Optional.empty();
    }

    @Override
    public List<DatabaseUser> findForUserIds(List<Long> ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM users WHERE id IN (:ids)",
                parameters,
                (rs, rowNum) -> {
                    return new DatabaseUser(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("name"));
                }
        );
    }
}
