package com.tokyo.beach.session;

import com.tokyo.beach.application.logon.LogonCredentials;
import com.tokyo.beach.application.session.DatabaseSessionRepository;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.session.UserSession;
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

    @Before
    public void setUp() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(buildDataSource());
        this.databaseSessionRepository = new DatabaseSessionRepository(this.jdbcTemplate);
    }

    @Test
    public void test_logon_returnsNewUserSession() throws Exception {
        try {
            LogonCredentials credentials = new LogonCredentials("jmiller@gmail.com", "password");
            TokenGenerator mockTokenGenerator = mock(TokenGenerator.class);
            when(mockTokenGenerator.nextToken()).thenReturn("new-token");

            insertUserIntoDatabase(credentials);


            Optional<UserSession> actualOptionalUserSession = databaseSessionRepository.create(
                    mockTokenGenerator,
                    credentials.getEmail(),
                    credentials.getPassword()
            );


            UserSession expectedUserSession = new UserSession(mockTokenGenerator, "jmiller@gmail.com");
            assertEquals(actualOptionalUserSession.get(), expectedUserSession);

        } finally {
            jdbcTemplate.update("TRUNCATE TABLE users CASCADE");
        }
    }

    @Test
    public void test_logonWithNonExistentUser_returnsEmptyOptional() {
        LogonCredentials credentials = new LogonCredentials("jmiller@gmail.com", "password");
        TokenGenerator mockGenerator = mock(TokenGenerator.class);
        when(mockGenerator.nextToken()).thenReturn("new-token");


        Optional<UserSession> actualOptionalUserSession = databaseSessionRepository.create(
                mockGenerator,
                credentials.getEmail(),
                credentials.getPassword()
        );


        assertFalse(actualOptionalUserSession.isPresent());
    }

    @Test
    public void test_logon_persistsToken_forValidCredentials() throws Exception {
        try {
            LogonCredentials credentials = new LogonCredentials("jmiller@gmail.com", "password");
            TokenGenerator mockTokenGenerator = mock(TokenGenerator.class);
            when(mockTokenGenerator.nextToken()).thenReturn("new-token");

            Number userId = insertUserIntoDatabase(credentials);
            System.out.println("userId = " + userId);


            databaseSessionRepository.create(
                    mockTokenGenerator,
                    credentials.getEmail(),
                    credentials.getPassword()
            );


            String sql = "SELECT token FROM session WHERE user_id = ?";
            List<String> persistedTokens = jdbcTemplate.queryForList(
                    sql,
                    String.class,
                    userId.intValue());

            assertEquals("new-token", persistedTokens.get(0));
        } finally {
            jdbcTemplate.update("TRUNCATE TABLE users CASCADE");
        }
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
