package com.tokyo.beach.session;

import com.tokyo.beach.restaurants.session.SessionDataMapper;
import com.tokyo.beach.restaurants.session.TokenGenerator;
import com.tokyo.beach.restaurants.session.UserSession;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.user.UserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionDataMapperTest {
    private SessionDataMapper sessionDataMapper;
    private JdbcTemplate jdbcTemplate;

    private TokenGenerator mockTokenGenerator;
    private User user;
    private Long userId;

    @Before
    public void setUp() throws Exception {
        this.jdbcTemplate = new JdbcTemplate(buildDataSource());
        this.sessionDataMapper = new SessionDataMapper(this.jdbcTemplate);

        mockTokenGenerator = mock(TokenGenerator.class);
        when(mockTokenGenerator.nextToken()).thenReturn("new-token");

        user = new UserFixture()
                .withEmail("jmiller@gmail.com")
                .withName("Jim Miller")
                .withPassword("password")
                .persist(jdbcTemplate);
        userId = user.getId();
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_create_returnsNewUserSession() throws Exception {
        UserSession actualUserSession = sessionDataMapper.create(
                mockTokenGenerator,
                user
        );

        UserSession expectedUserSession = new UserSession(mockTokenGenerator, "jmiller@gmail.com", "Jim Miller", userId);
        assertEquals(actualUserSession, expectedUserSession);
    }

    @Test
    public void test_create_persistsToken_forValidCredentials() throws Exception {
        sessionDataMapper.create(
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
        new SessionFixture()
                .withTokenValue("token-value")
                .withUserId(userId)
                .persist(jdbcTemplate);


        Optional<Long> maybeUserId = sessionDataMapper.validateToken("token-value");

        assertEquals(userId, maybeUserId.get());
    }

    @Test
    public void test_validateToken_returnsEmptyForInvalidCredentials() throws Exception {
        Optional<Long> maybeUserId = sessionDataMapper.validateToken("invalid-token");

        assertFalse(maybeUserId.isPresent());
    }

    @Test
    public void test_delete_removesSessionRecordForValidToken() throws Exception {
        new SessionFixture()
                .withTokenValue("token-value")
                .withUserId(userId)
                .persist(jdbcTemplate);

        sessionDataMapper.delete("token-value");

        String sql = "SELECT count(*) FROM session WHERE token = ?";
        int count = jdbcTemplate.queryForObject(
                sql,
                new Object[]{"token-value"},
                Integer.class
        );
        assertThat(count, is(0));
    }
}
