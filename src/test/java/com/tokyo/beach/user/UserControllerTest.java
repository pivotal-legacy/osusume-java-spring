package com.tokyo.beach.user;

import com.tokyo.beach.session.TokenGenerator;
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

//    @Test
//    public void login_acceptsUserNameAndPassword_andReturnsToken() throws Exception {
//        when(tokenGenerator.nextToken())
//                .thenReturn("abcde12345");
//
//        mvc.perform(MockMvcRequestBuilders.post("/auth/session")
//                .contentType("application/json;charset=UTF-8")
//                .content("{\"email\":\"jmiller\",\"password\":\"mypassword\"}")
//                .accept("application/json;charset=UTF-8")
//        )
//                .andExpect(status().isAccepted())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(content().string("{\"token\":\"abcde12345\"}"));
//    }

    @Test
    public void postToUser_returnsCreatedHttpStatus() throws Exception {
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
    public void postToUser_invokesUserRepoCreateMethod() throws Exception {
        when(tokenGenerator.nextToken())
                .thenReturn("abcde12345");

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType("application/json;charset=UTF8")
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept("application/json;charset=UTF8")
        );

        verify(userRepository, times(1)).create("jmiller@gmail.com", "mypassword");
    }

    @Test
    public void postToUser_returnsToken() throws Exception {
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
