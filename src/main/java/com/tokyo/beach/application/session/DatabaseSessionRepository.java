package com.tokyo.beach.application.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DatabaseSessionRepository implements SessionRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<UserSession> logon(TokenGenerator generator, String email, String password) {
        String sql = "SELECT count(*) FROM USERS WHERE email = ? AND password = ?";
        int count = this.jdbcTemplate.queryForObject(
                sql,
                new Object[] {
                        email,
                        password
                },
                Integer.class
        );

        if (count == 1) {
            return Optional.of(new UserSession(generator, email));
        }

        return Optional.empty();
    }
}
