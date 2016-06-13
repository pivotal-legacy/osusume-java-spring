package com.tokyo.beach.session;

import com.tokyo.beach.restaurants.session.DatabaseSessionRepository;
import com.tokyo.beach.restaurants.session.TokenGenerator;
import com.tokyo.beach.restaurants.session.UserSession;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.NewUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.TestDatabaseUtils.insertUserIntoDatabase;
import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseSessionRepositoryTest {
    private DatabaseSessionRepository databaseSessionRepository;
    private JdbcTemplate jdbcTemplate;

    private TokenGenerator mockTokenGenerator;
    private User user;
    private Long userId;

    @Before
    public void setUp() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(buildDataSource());
        this.databaseSessionRepository = new DatabaseSessionRepository(this.jdbcTemplate);

        NewUser newUser = new NewUser(
                "jmiller@gmail.com", "password", "Jim Miller"
        );
        mockTokenGenerator = mock(TokenGenerator.class);
        when(mockTokenGenerator.nextToken()).thenReturn("new-token");

        userId = insertUserIntoDatabase(jdbcTemplate, newUser).getId();
        user = new User(
                userId, newUser.getEmail(), newUser.getName()
        );
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_create_returnsNewUserSession() throws Exception {
        UserSession actualUserSession = databaseSessionRepository.create(
                mockTokenGenerator,
                user
        );


        UserSession expectedUserSession = new UserSession(mockTokenGenerator, "jmiller@gmail.com", "Jim Miller", userId);
        assertEquals(actualUserSession, expectedUserSession);
    }

    @Test
    public void test_create_persistsToken_forValidCredentials() throws Exception {
        databaseSessionRepository.create(
                mockTokenGenerator,
                user
        );


        String sql = "SELECT token FROM session WHERE user_id = ?";
        List<String> persistedTokens = jdbcTemplate.queryForList(
                sql,
                String.class,
                userId.intValue());

        assertEquals("new-token", persistedTokens.get(0));
    }

    @Test
    public void test_validateToken_returnsUserId() throws Exception {
        insertSessionIntoDatabase("token-value", userId);


        Optional<Long> maybeUserId = databaseSessionRepository.validateToken("token-value");


        assertEquals(userId, maybeUserId.get());
    }

    @Test
    public void test_validateToken_returnsEmptyForInvalidCredentials() throws Exception {
        Optional<Long> maybeUserId = databaseSessionRepository.validateToken("invalid-token");


        assertFalse(maybeUserId.isPresent());
    }

    @Test
    public void test_delete_removesSessionRecordForValidToken() throws Exception {
        insertSessionIntoDatabase("token-value", userId.intValue());


        databaseSessionRepository.delete("token-value");


        String sql = "SELECT count(*) FROM session WHERE token = ?";
        int count = jdbcTemplate.queryForObject(
                sql,
                new Object[]{"token-value"},
                Integer.class
        );
        assertThat(count, is(0));
    }

    private void insertSessionIntoDatabase(String token, long userId) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("session")
                .usingColumns("token", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("user_id", userId);

        insert.execute(params);
    }
}
