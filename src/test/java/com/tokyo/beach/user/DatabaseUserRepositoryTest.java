package com.tokyo.beach.user;

import com.tokyo.beach.application.user.LogonCredentials;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.DatabaseUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.tokyo.beach.ControllerTestingUtils.buildDataSource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class DatabaseUserRepositoryTest {

    private DatabaseUserRepository databaseUserRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(buildDataSource());
        this.databaseUserRepository = new DatabaseUserRepository(this.jdbcTemplate);
    }

    @Test
    public void test_create_insertsUserCredentialsIntoDB() throws Exception {
        try {
            LogonCredentials credentials = new LogonCredentials("jmiller@gmail.com", "myPassword123");

            String sql = "SELECT count(*) FROM users WHERE email = ?";
            int count = this.jdbcTemplate.queryForObject(
                    sql, new Object[]{credentials.getEmail()}, Integer.class
            );
            assertThat(count, is(0));


            DatabaseUser user = this.databaseUserRepository.create(
                    credentials.getEmail(),
                    credentials.getPassword()
            );


            sql = "SELECT count(*) FROM USERS WHERE email = ?";
            count = this.jdbcTemplate.queryForObject(
                    sql, new Object[]{credentials.getEmail()}, Integer.class
            );
            assertThat(count, is(1));
            assertThat(user.getId().intValue(), is(greaterThan(0)));
            assertThat(user.getEmail(), is("jmiller@gmail.com"));
        } finally {
            this.jdbcTemplate.update("TRUNCATE TABLE users CASCADE");
        }
    }

    @Test
    public void test_getExistingUserWithCredentials_returnsUser() throws Exception {
        try {
            LogonCredentials credentials = new LogonCredentials("user@gmail.com", "password");
            insertUserIntoDatabase(credentials);


            Optional<DatabaseUser> maybeUser = databaseUserRepository.get(credentials);


            assertThat(maybeUser.get().getId().intValue(), is(greaterThan(0)));
            assertThat(maybeUser.get().getEmail(), is("user@gmail.com"));
        } finally {
            jdbcTemplate.update("TRUNCATE TABLE users CASCADE");
        }
    }

    @Test
    public void test_getNonExistentUser_returnsEmptyOptional() throws Exception {
        LogonCredentials credentials = new LogonCredentials("user@gmail.com", "password");


        Optional<DatabaseUser> maybeUser = databaseUserRepository.get(credentials);


        assertFalse(maybeUser.isPresent());
    }

    private Number insertUserIntoDatabase(LogonCredentials credentials) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "password")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("email", credentials.getEmail());
        params.put("password", credentials.getPassword());

        return insert.executeAndReturnKey(params);
    }
}
