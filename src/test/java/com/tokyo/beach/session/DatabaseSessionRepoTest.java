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
            TokenGenerator mockGenerator = mock(TokenGenerator.class);
            when(mockGenerator.nextToken()).thenReturn("new-token");

            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingColumns("email", "password")
                    .usingGeneratedKeyColumns("id");

            Map<String, Object> params = new HashMap<>();
            params.put("email", credentials.getEmail());
            params.put("password", credentials.getPassword());

            insert.executeAndReturnKey(params);


            Optional<UserSession> actualOptionalUserSession = databaseSessionRepository.logon(
                    mockGenerator,
                    credentials.getEmail(),
                    credentials.getPassword()
            );


            UserSession expectedUserSession = new UserSession(mockGenerator, "jmiller@gmail.com");
            assertEquals(actualOptionalUserSession.get(), expectedUserSession);

        } finally {
            jdbcTemplate.update("TRUNCATE TABLE users");
        }
    }

    @Test
    public void test_logonWithAbsentUser_returnsEmptyOptional() {
        LogonCredentials credentials = new LogonCredentials("jmiller@gmail.com", "password");
        TokenGenerator mockGenerator = mock(TokenGenerator.class);
        when(mockGenerator.nextToken()).thenReturn("new-token");


        Optional<UserSession> actualOptionalUserSession = databaseSessionRepository.logon(
                mockGenerator,
                credentials.getEmail(),
                credentials.getPassword()
        );


        assertFalse(actualOptionalUserSession.isPresent());
    }
}
