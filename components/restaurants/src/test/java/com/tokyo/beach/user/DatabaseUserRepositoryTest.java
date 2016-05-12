package com.tokyo.beach.user;

import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.DatabaseUserRepository;
import com.tokyo.beach.restaurants.session.LogonCredentials;
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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
        NewUser newUser = new NewUser(
                "jmiller@gmail.com", "myPassword123", "Joe Miller"
        );

        String sql = "SELECT count(*) FROM users WHERE email = ?";
        int count = this.jdbcTemplate.queryForObject(
                sql, new Object[]{newUser.getEmail()}, Integer.class
        );
        assertThat(count, is(0));


        User user = this.databaseUserRepository.create(
                newUser.getEmail(),
                newUser.getPassword(),
                newUser.getName()
        );


        sql = "SELECT count(*) FROM USERS WHERE email = ?";
        count = this.jdbcTemplate.queryForObject(
                sql, new Object[]{newUser.getEmail()}, Integer.class
        );
        assertThat(count, is(1));
        assertTrue(user.getId() > 0);
        assertThat(user.getEmail(), is("jmiller@gmail.com"));
    }

    @Test
    public void test_getExistingUserWithCredentials_returnsUser() throws Exception {
        NewUser newUser = new NewUser(
                "user@gmail.com", "password", "Username"
        );
        insertUserIntoDatabase(jdbcTemplate, newUser);


        Optional<User> maybeUser = databaseUserRepository.get(
                new LogonCredentials(newUser.getEmail(), newUser.getPassword())
        );


        assertTrue(maybeUser.get().getId() > 0);
        assertThat(maybeUser.get().getEmail(), is("user@gmail.com"));
    }

    @Test
    public void test_getExistingUser_isCaseInsenitive() throws Exception {
        NewUser newUser = new NewUser(
                "user@gmail.com", "password", "Username"
        );
        insertUserIntoDatabase(jdbcTemplate, newUser);


        LogonCredentials queryCredentials = new LogonCredentials("User@gMail.com", "password");
        Optional<User> maybeUser = databaseUserRepository.get(queryCredentials);


        assertTrue(maybeUser.isPresent());
    }

    @Test
    public void test_getNonExistentUser_returnsEmptyOptional() throws Exception {
        LogonCredentials credentials = new LogonCredentials("user@gmail.com", "password");


        Optional<User> maybeUser = databaseUserRepository.get(credentials);


        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void test_getExistingUserWithUserId_returnsUser() throws Exception {
        NewUser newUser = new NewUser(
                "user@gmail.com", "password", "Username"
        );
        Number userId = insertUserIntoDatabase(jdbcTemplate, newUser).getId();


        Optional<User> maybeUser = databaseUserRepository.get(userId.longValue());


        assertTrue(maybeUser.get().getId() == userId.longValue());
        assertThat(maybeUser.get().getEmail(), is("user@gmail.com"));
    }

    @Test
    public void test_getNonExistentUserWithUserId_returnsEmptyOptional() throws Exception {
        Optional<User> maybeUser = databaseUserRepository.get(999);


        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void test_findForUserIds_returnsDatabaseUserList() throws Exception {
        jdbcTemplate.update("INSERT INTO users (id, email, name) " +
                "VALUES (1, 'jiro@user.com', 'jiro')");

        List<User> users = databaseUserRepository.findForUserIds(
                singletonList(1L)
        );

        assertEquals(users.size(), 1);

        User user = users.get(0);
        assertEquals(user.getName(), "jiro");
    }
}
