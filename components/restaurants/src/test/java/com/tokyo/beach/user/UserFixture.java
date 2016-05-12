package com.tokyo.beach.user;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.restaurants.user.User;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserFixture {
    private long id = 0;
    private String name = "Not Specified";
    private String email = "Not Specified";

    public UserFixture withId(long id) {
        this.id = id;
        return this;
    }

    public UserFixture withName(String name) {
        this.name = name;
        return this;
    }

    public UserFixture withEmail(String email) {
        this.email = email;
        return this;
    }

    public User build() {
        return new User(
                id,
                email,
                name
        );
    }

    public User persist(JdbcTemplate jdbcTemplate) {
        NewUser newUser = new NewUser(
                email,
                null,
                name
        );

        return TestDatabaseUtils.insertUserIntoDatabase(
                jdbcTemplate,
                newUser
        );
    }
}
