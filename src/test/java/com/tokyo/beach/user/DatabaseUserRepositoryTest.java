package com.tokyo.beach.user;

import com.tokyo.beach.application.logon.LogonCredentials;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.DatabaseUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

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

    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
