package com.tokyo.beach.session;

import org.springframework.jdbc.core.JdbcTemplate;

import static com.tokyo.beach.TestDatabaseUtils.insertSessionIntoDatabase;

public class SessionFixture {
    private String tokenValue;
    private long userId;

    public SessionFixture withTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
        return this;
    }

    public SessionFixture withUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public void persist(JdbcTemplate jdbcTemplate) {
        insertSessionIntoDatabase(jdbcTemplate, tokenValue, userId);
    }
}
