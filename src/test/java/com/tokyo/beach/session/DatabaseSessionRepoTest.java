package com.tokyo.beach.session;

import com.tokyo.beach.application.user.LogonCredentials;
import com.tokyo.beach.application.session.DatabaseSessionRepository;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.session.UserSession;
import com.tokyo.beach.application.user.DatabaseUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tokyo.beach.ControllerTestingUtils.buildDataSource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseSessionRepoTest {
    private DatabaseSessionRepository databaseSessionRepository;
    private JdbcTemplate jdbcTemplate;

    private TokenGenerator mockTokenGenerator;
    private DatabaseUser user;
    private Number userId;

    @Before
    public void setUp() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(buildDataSource());
        this.databaseSessionRepository = new DatabaseSessionRepository(this.jdbcTemplate);

        LogonCredentials credentials = new LogonCredentials("jmiller@gmail.com", "password");
        mockTokenGenerator = mock(TokenGenerator.class);
        when(mockTokenGenerator.nextToken()).thenReturn("new-token");

        userId = insertUserIntoDatabase(credentials);
        user = new DatabaseUser(userId.intValue(), credentials.getEmail());
    }

    @After
    public void tearDown() throws Exception {
        jdbcTemplate.update("TRUNCATE TABLE users CASCADE");
    }

    @Test
    public void test_create_returnsNewUserSession() throws Exception {
        UserSession actualUserSession = databaseSessionRepository.create(
                mockTokenGenerator,
                user
        );


        UserSession expectedUserSession = new UserSession(mockTokenGenerator, "jmiller@gmail.com");
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
        insertSessionIntoDatabase("token-value", userId.intValue());


        Optional<Number> returnedUserId = databaseSessionRepository.validateToken("token-value");


        assertEquals(userId, returnedUserId.get());
    }

    @Test
    public void test_validateToken_returnsEmptyForInvalidCredentials() throws Exception {
        Optional<Number> returnedUserId = databaseSessionRepository.validateToken("invalid-token");


        assertFalse(returnedUserId.isPresent());
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

    private void insertSessionIntoDatabase(String token, int userId) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("session")
                .usingColumns("token", "user_id");

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("user_id", userId);

        insert.execute(params);
    }

}
