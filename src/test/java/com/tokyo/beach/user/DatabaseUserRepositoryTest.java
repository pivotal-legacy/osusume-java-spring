package osusume.User;

import com.tokyo.beach.logon.LogonCredentials;
import com.tokyo.beach.user.DatabaseUser;
import com.tokyo.beach.user.DatabaseUserRepository;
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
    public void userRepo_createsUserWithCredentials() throws Exception {
        try {
            LogonCredentials credentials = new LogonCredentials("Joe", "myPassword123");

            String sql = "SELECT count(*) FROM USERS WHERE email = ?";
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
            assertThat(user.getEmail(), is("Joe"));
        } finally {
            this.jdbcTemplate.update("TRUNCATE TABLE users");
        }
    }


    // Cannot create user if required fields (email or password) are missing.


    // Cannot create user if email is already taken


    // what other tests are needed?


    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume_localtest");
        return dataSource;
    }
}
