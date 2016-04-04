package com.tokyo.beach.application.session;

import com.tokyo.beach.application.user.DatabaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

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
}
