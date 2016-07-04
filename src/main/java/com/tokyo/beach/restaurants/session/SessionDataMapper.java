package com.tokyo.beach.restaurants.session;

import com.tokyo.beach.restaurants.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SessionDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SessionDataMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserSession create(TokenGenerator generator, User user) {
        UserSession userSession = new UserSession(generator, user.getEmail(), user.getName(), user.getId());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("session")
                .usingColumns("token", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("token", userSession.getToken());
        params.put("user_id", user.getId());

        insert.execute(params);

        return userSession;
    }

    public Optional<Long> validateToken(String token) {
        List<Long> userIds = jdbcTemplate.query(
                "SELECT user_id FROM session where token = ?",
               (rs, rowNum) -> rs.getLong("user_id"),
               token
        );

        if (userIds.size() == 1) {
            return Optional.of(userIds.get(0));
        }
        return Optional.empty();
    }

    public void delete(String token) {
        jdbcTemplate.update(
                "DELETE from session WHERE token = ?",
                token
        );
    }
}
