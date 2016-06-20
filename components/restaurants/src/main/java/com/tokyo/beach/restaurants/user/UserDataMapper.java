package com.tokyo.beach.restaurants.user;

import com.tokyo.beach.restaurants.session.LogonCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDataMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User create(String email, String password, String name) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password", "name")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);

        Number id = insert.executeAndReturnKey(params);

        return new User(id.longValue(), email, name);
    }

    public Optional<User> get(LogonCredentials credentials) {
        String sql = "SELECT id, email, name FROM users WHERE lower(email) = ? AND password = ?";
        List<User> users = jdbcTemplate.query(
                sql,
                UserDataMapper::mapRow,
                credentials.getEmail().toLowerCase(),
                credentials.getPassword()
        );

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        }

        return Optional.empty();
    }

    public Optional<User> get(long userId) {
        String sql = "SELECT id, email, name FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(
                sql,
                UserDataMapper::mapRow,
                userId
        );

        if (users.size() == 1) {
            return Optional.of(users.get(0));
        }

        return Optional.empty();
    }

    public List<User> findForUserIds(List<Long> ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM users WHERE id IN (:ids)",
                parameters,
                UserDataMapper::mapRow
        );
    }

    public User findForRestaurantId(long restaurantId) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = " +
                        "(SELECT created_by_user_id FROM restaurant WHERE id = ?)",
                UserDataMapper::mapRow,
                restaurantId
        );
    }

    private static User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("name")
        );
    }
}
