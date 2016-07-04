package com.tokyo.beach.user;

import com.tokyo.beach.cuisine.CuisineFixture;
import com.tokyo.beach.pricerange.PriceRangeFixture;
import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import com.tokyo.beach.restaurants.session.LogonCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UserDataMapperTest {

    private UserDataMapper userDataMapper;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(buildDataSource());
        this.userDataMapper = new UserDataMapper(this.jdbcTemplate);
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


        User user = this.userDataMapper.create(
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
        User user = new UserFixture()
                .withEmail("user@gmail.com")
                .withPassword("password")
                .persist(jdbcTemplate);

        Optional<User> maybeUser = userDataMapper.get(
                new LogonCredentials(user.getEmail(), "password")
        );

        assertTrue(maybeUser.get().getId() > 0);
        assertThat(maybeUser.get().getEmail(), is("user@gmail.com"));
    }

    @Test
    public void test_getExistingUser_isCaseInsenitive() throws Exception {
        new UserFixture()
                .withEmail("user@gmail.com")
                .withPassword("password")
                .persist(jdbcTemplate);

        LogonCredentials queryCredentials = new LogonCredentials("User@gMail.com", "password");
        Optional<User> maybeUser = userDataMapper.get(queryCredentials);

        assertTrue(maybeUser.isPresent());
    }

    @Test
    public void test_getNonExistentUser_returnsEmptyOptional() throws Exception {
        LogonCredentials credentials = new LogonCredentials("user@gmail.com", "password");

        Optional<User> maybeUser = userDataMapper.get(credentials);

        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void test_getExistingUserWithUserId_returnsUser() throws Exception {
        User user = new UserFixture()
                .withEmail("user@gmail.com")
                .withPassword("password")
                .persist(jdbcTemplate);

        Optional<User> maybeUser = userDataMapper.get(user.getId());

        assertTrue(maybeUser.get().getId() == user.getId());
        assertThat(maybeUser.get().getEmail(), is(user.getEmail()));
    }

    @Test
    public void test_getNonExistentUserWithUserId_returnsEmptyOptional() throws Exception {
        Optional<User> maybeUser = userDataMapper.get(999);

        assertFalse(maybeUser.isPresent());
    }

    @Test
    public void test_findForUserIds_returnsDatabaseUserList() throws Exception {
        jdbcTemplate.update("INSERT INTO users (id, email, name) " +
                "VALUES (1, 'jiro@user.com', 'jiro')");

        List<User> users = userDataMapper.findForUserIds(
                singletonList(1L)
        );

        assertEquals(users.size(), 1);

        User user = users.get(0);
        assertEquals(user.getName(), "jiro");
    }

    @Test
    public void test_findForRestaurant_returnsTheUserThatCreatedTheRestaurant() throws Exception {
        User user = new UserFixture().persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture()
                .withPriceRange(new PriceRangeFixture().persist(jdbcTemplate))
                .withCuisine(new CuisineFixture().persist(jdbcTemplate))
                .withUser(user)
                .persist(jdbcTemplate);

        User foundUser = userDataMapper.findForRestaurantId(restaurant.getId());
        assertEquals(foundUser, user);
    }
}
