package com.tokyo.beach.user;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.token.UserSession;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserController;
import com.tokyo.beach.application.user.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {
    private MockMvc mvc;
    private UserRepository userRepository;
    private TokenGenerator tokenGenerator;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        tokenGenerator = mock(TokenGenerator.class);
        mvc = MockMvcBuilders.standaloneSetup(new UserController(
                userRepository,
                tokenGenerator)
        )
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();
    }

    @Test
    public void test_postToAuthSession_returnsAcceptedHttpStatus() throws Exception {
        UserSession userSession = new UserSession(tokenGenerator, "jmiller@gmail.com");
        when(userRepository.logon(tokenGenerator, "jmiller@gmail.com", "mypassword"))
                .thenReturn(Optional.of(userSession));

        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_postToAuthSession_invokesUserRepoLogonMethod() throws Exception {
        when(userRepository.logon(tokenGenerator, "jmiller@gmail.com", "mypassword"))
                .thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        );

        verify(userRepository, times(1)).logon(tokenGenerator, "jmiller@gmail.com", "mypassword");
    }

    @Test
    public void test_postToAuthSession_returnsValidSession() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");

        UserSession userSession = new UserSession(tokenGenerator, "jmiller@gmail.com");
        when(userRepository.logon(tokenGenerator, "jmiller@gmail.com", "mypassword"))
                .thenReturn(Optional.of(userSession));

        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
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
        when(userRepository.logon(tokenGenerator, "not valid", "not valid"))
                .thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"not valid\",\"password\":\"not valid\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Invalid email or password.\"}"));
    }

    @Test
    public void test_postToUser_returnsCreatedHttpStatus() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF8")
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void test_postToUser_invokesUserRepoCreateMethod() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF8")
        );

        verify(userRepository, times(1)).create("jmiller@gmail.com", "mypassword");
    }

    @Test
    public void test_postToUser_returnsToken() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");
        when(userRepository.create("jmiller@gmail.com", "mypassword"))
                .thenReturn(new DatabaseUser(6, "jmiller@gmail.com"));

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF8")
        )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("{\"id\":6,\"email\":\"jmiller@gmail.com\"}"));
    }
}
