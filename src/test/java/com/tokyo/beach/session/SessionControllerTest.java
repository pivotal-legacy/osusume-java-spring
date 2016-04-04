package com.tokyo.beach.session;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import com.tokyo.beach.application.session.SessionController;
import com.tokyo.beach.application.session.SessionRepository;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.token.UserSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static com.tokyo.beach.ControllerTestingUtils.createControllerAdvice;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SessionControllerTest {
    private MockMvc mvc;
    private SessionRepository sessionRepository;
    private TokenGenerator tokenGenerator;

    @Before
    public void setUp() throws Exception {
        sessionRepository = mock(SessionRepository.class);
        tokenGenerator = mock(TokenGenerator.class);
        mvc = MockMvcBuilders.standaloneSetup(new SessionController(
                sessionRepository,
                tokenGenerator)
        )
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();
    }

    @Test
    public void test_postToAuthSession_returnsAcceptedHttpStatus() throws Exception {
        UserSession userSession = new UserSession(tokenGenerator, "jmiller@gmail.com");
        when(sessionRepository.logon(tokenGenerator, "jmiller@gmail.com", "mypassword"))
                .thenReturn(Optional.of(userSession));

        mvc.perform(MockMvcRequestBuilders.post("/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_postToAuthSession_invokesUserRepoLogonMethod() throws Exception {
        when(sessionRepository.logon(tokenGenerator, "jmiller@gmail.com", "mypassword"))
                .thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.post("/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        );

        verify(sessionRepository, times(1)).logon(tokenGenerator, "jmiller@gmail.com", "mypassword");
    }

    @Test
    public void test_postToAuthSession_returnsValidSession() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");

        UserSession userSession = new UserSession(tokenGenerator, "jmiller@gmail.com");
        when(sessionRepository.logon(tokenGenerator, "jmiller@gmail.com", "mypassword"))
                .thenReturn(Optional.of(userSession));

        mvc.perform(MockMvcRequestBuilders.post("/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.email", is("jmiller@gmail.com")))
                .andExpect(jsonPath("$.token", is("abcde12345")));
    }

    @Test
    public void test_postToAuthSessionWithInvalidUserCredentials_throwsException() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");
        when(sessionRepository.logon(tokenGenerator, "not valid", "not valid"))
                .thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.post("/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"not valid\",\"password\":\"not valid\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Invalid email or password.\"}"));
    }
}
