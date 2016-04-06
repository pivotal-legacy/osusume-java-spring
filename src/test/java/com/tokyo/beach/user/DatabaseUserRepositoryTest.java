package com.tokyo.beach.user;

import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.DatabaseUserRepository;
import com.tokyo.beach.application.user.LogonCredentials;
import com.tokyo.beach.application.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static com.tokyo.beach.TestUtils.buildDataSource;
import static com.tokyo.beach.TestUtils.insertUserIntoDatabase;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
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

    @After
    public void tearDown() throws Exception {
        this.jdbcTemplate.update("TRUNCATE TABLE users CASCADE");
    }

    @Test
    public void test_create_insertsUserCredentialsIntoDB() throws Exception {
        UserRegistration userRegistration = new UserRegistration(
                "jmiller@gmail.com", "myPassword123", "Joe Miller"
        );

        String sql = "SELECT count(*) FROM users WHERE email = ?";
        int count = this.jdbcTemplate.queryForObject(
                sql, new Object[]{userRegistration.getEmail()}, Integer.class
        );
        assertThat(count, is(0));


        DatabaseUser user = this.databaseUserRepository.create(
                userRegistration.getEmail(),
                userRegistration.getPassword(),
                userRegistration.getName()
        );


        sql = "SELECT count(*) FROM USERS WHERE email = ?";
        count = this.jdbcTemplate.queryForObject(
                sql, new Object[]{userRegistration.getEmail()}, Integer.class
        );
        assertThat(count, is(1));
        assertTrue(user.getId() > 0);
        assertThat(user.getEmail(), is("jmiller@gmail.com"));
    }

    @Test
    public void test_getExistingUserWithCredentials_returnsUser() throws Exception {
        UserRegistration userRegistration = new UserRegistration(
                "user@gmail.com", "password", "Username"
        );
        insertUserIntoDatabase(jdbcTemplate, userRegistration);


        Optional<DatabaseUser> maybeUser = databaseUserRepository.get(
                new LogonCredentials(userRegistration.getEmail(), userRegistration.getPassword())
        );


        assertTrue(maybeUser.get().getId() > 0);
        assertThat(maybeUser.get().getEmail(), is("user@gmail.com"));
    }

    @Test
    public void test_getExistingUser_isCaseInsenitive() throws Exception {
        UserRegistration userRegistration = new UserRegistration(
                "user@gmail.com", "password", "Username"
        );
        insertUserIntoDatabase(jdbcTemplate, userRegistration);


        LogonCredentials queryCredentials = new LogonCredentials("User@gMail.com", "password");
        Optional<DatabaseUser> maybeUser = databaseUserRepository.get(queryCredentials);


        assertTrue(maybeUser.isPresent());
    }

    @Test
    public void test_getNonExistentUser_returnsEmptyOptional() throws Exception {
        LogonCredentials credentials = new LogonCredentials("user@gmail.com", "password");


        Optional<DatabaseUser> maybeUser = databaseUserRepository.get(credentials);


        assertFalse(maybeUser.isPresent());
    }
}
