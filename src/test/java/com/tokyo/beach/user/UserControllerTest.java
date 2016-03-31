package com.tokyo.beach.user;

import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserController;
import com.tokyo.beach.application.user.UserRepository;
import com.tokyo.beach.application.session.TokenGenerator;
import com.tokyo.beach.application.token.UserSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
                .build();
    }

    @Test
    public void test_postToAuthSession_returnsAcceptedHttpStatus() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
                .contentType("application/json;charset=UTF-8")
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(status().isAccepted());
    }

    @Test
    public void test_postToAuthSession_invokesUserRepoLogonMethod() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
                .contentType("application/json;charset=UTF-8")
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
                .thenReturn(userSession);

        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
                .contentType("application/json;charset=UTF-8")
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF-8")
        )
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string("{\"email\":\"jmiller@gmail.com\",\"token\":\"abcde12345\"}"));
    }

    @Test
    public void test_postToUser_returnsCreatedHttpStatus() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType("application/json;charset=UTF8")
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF8")
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void test_postToUser_invokesUserRepoCreateMethod() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType("application/json;charset=UTF8")
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
                .contentType("application/json;charset=UTF8")
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF8")
        )
                .andExpect(content().contentType("application/json;charset=UTF8"))
                .andExpect(content().string("{\"id\":6,\"email\":\"jmiller@gmail.com\"}"));
    }
}
