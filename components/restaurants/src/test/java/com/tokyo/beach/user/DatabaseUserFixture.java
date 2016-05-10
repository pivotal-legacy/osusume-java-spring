package com.tokyo.beach.user;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.user.DatabaseUser;
import com.tokyo.beach.restaurants.user.UserRegistration;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseUserFixture {
    private long id = 0;
    private String name = "Not Specified";
    private String email = "Not Specified";

    public DatabaseUserFixture withId(long id) {
        this.id = id;
        return this;
    }

    public DatabaseUserFixture withName(String name) {
        this.name = name;
        return this;
    }

    public DatabaseUserFixture withEmail(String email) {
        this.email = email;
        return this;
    }

    public DatabaseUser build() {
        return new DatabaseUser(
                id,
                email,
                name
        );
    }

    public DatabaseUser persist(JdbcTemplate jdbcTemplate) {
        UserRegistration userRegistration = new UserRegistration(
                email,
                null,
                name
        );

        return TestDatabaseUtils.insertUserIntoDatabase(
                jdbcTemplate,
                userRegistration
        );
    }
}
