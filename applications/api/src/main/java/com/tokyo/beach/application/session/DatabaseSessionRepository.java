package com.tokyo.beach.application.session;

import com.tokyo.beach.application.user.DatabaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DatabaseSessionRepository implements SessionRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserSession create(TokenGenerator generator, DatabaseUser user) {
        UserSession userSession = new UserSession(generator, user.getEmail());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("session")
                .usingColumns("token", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("token", userSession.getToken());
        params.put("user_id", user.getId());

        insert.execute(params);

        return userSession;
    }

    @Override
    public Optional<Long> validateToken(String token) {
        List<Long> userIds = jdbcTemplate.query(
                "SELECT user_id FROM session where token = ?",
               (rs, rowNum) -> {
                   return  rs.getLong("user_id");
               },
               token);

        if (userIds.size() == 1) {
            return Optional.of(userIds.get(0));
        }
        return Optional.empty();
    }

    @Override
    public void delete(String token) {
        jdbcTemplate.update(
                "DELETE from session WHERE token = ?",
                token
        );
    }
}
