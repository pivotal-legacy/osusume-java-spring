package com.tokyo.beach.application.user;

import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.token.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
}
