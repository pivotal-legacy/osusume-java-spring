package com.tokyo.beach.user;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserController;
import com.tokyo.beach.application.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.tokyo.beach.ControllerTestingUtils.createControllerAdvice;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {
    private MockMvc mvc;
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(new UserController(
                userRepository)
        )
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();
    }

    @Test
    public void test_postToUser_returnsCreatedHttpStatus() throws Exception {
        mvc.perform(post("/users")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void test_postToUser_invokesUserRepoCreateMethod() throws Exception {
        mvc.perform(post("/users")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        );

        verify(userRepository, times(1)).create("jmiller@gmail.com", "mypassword");
    }

    @Test
    public void test_postToUser_returnsToken() throws Exception {
        when(userRepository.create("jmiller@gmail.com", "mypassword"))
                .thenReturn(new DatabaseUser(6, "jmiller@gmail.com"));

        mvc.perform(post("/users")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("{\"id\":6,\"email\":\"jmiller@gmail.com\"}"));
    }

    @Test
    public void test_unauthenticated_returnsBadRequestHttpStatus() throws Exception {
        mvc.perform(get("/unauthenticated"))
                .andExpect(status().isBadRequest());
    }
}
