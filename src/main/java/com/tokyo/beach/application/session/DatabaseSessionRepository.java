package com.tokyo.beach.application.session;

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
    public Optional<UserSession> create(TokenGenerator generator, String email, String password) {
        String sql = "SELECT id FROM USERS WHERE email = ? AND password = ?";
        List<Integer> userIds = this.jdbcTemplate.queryForList(
                sql,
                Integer.class,
                email,
                password);

        if (userIds.size() == 1) {
            int userId = userIds.get(0);

            UserSession userSession = new UserSession(generator, email);

            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("session")
                    .usingColumns("token", "user_id");

            Map<String, Object> params = new HashMap<>();
            params.put("token", userSession.getToken());
            params.put("user_id", userId);

            insert.execute(params);

            return Optional.of(userSession);
        }

        return Optional.empty();
    }
}
