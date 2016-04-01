package com.tokyo.beach.user;

import com.tokyo.beach.application.logon.LogonCredentials;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.token.UserSession;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.DatabaseUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
            this.jdbcTemplate.update("TRUNCATE TABLE users");
        }
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


            Optional<UserSession> actualOptionalUserSession = databaseUserRepository.logon(
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


        Optional<UserSession> actualOptionalUserSession = databaseUserRepository.logon(
                mockGenerator,
                credentials.getEmail(),
                credentials.getPassword()
        );


        assertFalse(actualOptionalUserSession.isPresent());
    }

    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
