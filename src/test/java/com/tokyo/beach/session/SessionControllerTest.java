package com.tokyo.beach.session;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import com.tokyo.beach.application.session.SessionController;
import com.tokyo.beach.application.session.SessionRepository;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.session.UserSession;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.LogonCredentials;
import com.tokyo.beach.application.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static com.tokyo.beach.ControllerTestingUtils.createControllerAdvice;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SessionControllerTest {
    private MockMvc mvc;
    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private TokenGenerator tokenGenerator;

    private LogonCredentials credentials;
    private Optional<DatabaseUser> maybeUser;

    @Before
    public void setUp() throws Exception {
        sessionRepository = mock(SessionRepository.class);
        userRepository = mock(UserRepository.class);
        tokenGenerator = mock(TokenGenerator.class);
        mvc = MockMvcBuilders.standaloneSetup(new SessionController(
                sessionRepository,
                userRepository,
                tokenGenerator)
        )
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();

        credentials = new LogonCredentials("jmiller@gmail.com", "mypassword");
        maybeUser = Optional.of(new DatabaseUser(999, "jmiller@gmail.com", "Joe Miller"));
        when(userRepository.get(credentials))
                .thenReturn(maybeUser);
    }

    @Test
    public void test_postToSession_returnsAcceptedHttpStatus() throws Exception {
        UserSession userSession = new UserSession(tokenGenerator, "jmiller@gmail.com");
        when(sessionRepository.create(tokenGenerator, maybeUser.get()))
                .thenReturn(userSession);


        mvc.perform(post("/session")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_postToSession_invokesSessionRepoCreate() throws Exception {
        mvc.perform(post("/session")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        );

        verify(sessionRepository, times(1)).create(tokenGenerator, maybeUser.get());
    }

    @Test
    public void test_postToSession_returnsValidSession() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");
        UserSession userSession = new UserSession(tokenGenerator, "jmiller@gmail.com");
        when(sessionRepository.create(tokenGenerator, maybeUser.get()))
                .thenReturn(userSession);


        mvc.perform(post("/session")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.email", is("jmiller@gmail.com")))
                .andExpect(jsonPath("$.token", is("abcde12345")));
    }

    @Test
    public void test_postToSessionWithInvalidUserCredentials_throwsException() throws Exception {
        when(userRepository.get(anyObject()))
                .thenReturn(Optional.empty());


        mvc.perform(post("/session")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"invalid@email\",\"password\":\"invalid password\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Invalid email or password.\"}"));
    }

    @Test
    public void test_deleteSession_returnsAcceptedHttpStatus() throws Exception {


        mvc.perform(delete("/session")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"token\":\"ABCDE12345\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_deleteSession_invokesSessionRepoLogout() throws Exception {


        mvc.perform(delete("/session")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"token\":\"ABCDE12345\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        );


        verify(sessionRepository, times(1)).delete("ABCDE12345");
    }

    @Test
    public void test_unauthenticated_returnsBadRequestHttpStatus() throws Exception {
        mvc.perform(get("/unauthenticated"))
                .andExpect(status().isBadRequest());
    }
}
