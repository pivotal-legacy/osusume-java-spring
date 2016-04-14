package com.tokyo.beach.user;

import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.DatabaseUserRepository;
import com.tokyo.beach.application.user.LogonCredentials;
import com.tokyo.beach.application.user.UserRegistration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.TestDatabaseUtils.insertUserIntoDatabase;
import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
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
        truncateAllTables(jdbcTemplate);
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

    @Test
    public void test_getExistingUserWithUserId_returnsUser() throws Exception {
        UserRegistration userRegistration = new UserRegistration(
                "user@gmail.com", "password", "Username"
        );
        Number userId = insertUserIntoDatabase(jdbcTemplate, userRegistration);


        Optional<DatabaseUser> maybeUser = databaseUserRepository.get(userId.longValue());


        assertTrue(maybeUser.get().getId() == userId.longValue());
        assertThat(maybeUser.get().getEmail(), is("user@gmail.com"));
    }

    @Test
    public void test_getNonExistentUserWithUserId_returnsEmptyOptional() throws Exception {
        Optional<DatabaseUser> maybeUser = databaseUserRepository.get(999);


        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void test_findForUserIds_returnsDatabaseUserList() throws Exception {
        jdbcTemplate.update("INSERT INTO users (id, email, name) " +
                "VALUES (1, 'jiro@user.com', 'jiro')");

        List<DatabaseUser> databaseUsers = databaseUserRepository.findForUserIds(
                singletonList(1L)
        );

        assertEquals(databaseUsers.size(), 1);

        DatabaseUser databaseUser = databaseUsers.get(0);
        assertEquals(databaseUser.getName(), "jiro");
    }
}
