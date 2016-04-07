package com.tokyo.beach.user;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserController;
import com.tokyo.beach.application.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

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
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\",\"name\":\"Joe Miller\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        );

        verify(userRepository, times(1)).create("jmiller@gmail.com", "mypassword", "Joe Miller");
    }

    @Test
    public void test_postToUser_returnsUserObject() throws Exception {
        when(userRepository.create("jmiller@gmail.com", "mypassword", "Joe Miller"))
                .thenReturn(new DatabaseUser(6, "jmiller@gmail.com", "Joe Miller"));

        mvc.perform(post("/users")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"email\":\"jmiller@gmail.com\",\"password\":\"mypassword\",\"name\":\"Joe Miller\"}")
                .accept(APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("{\"id\":6,\"email\":\"jmiller@gmail.com\",\"name\":\"Joe Miller\"}"));
    }

    @Test
    public void test_getUser_returnsUserObject() throws Exception {
        when(userRepository.get(12))
                .thenReturn(Optional.of(
                        new DatabaseUser(12, "jmiller@gmail.com", "Joe Miller")
                ));


        mvc.perform(get("/profile")
                .requestAttr("userId", 12)
        )
                .andExpect(content().string("{\"id\":12,\"email\":\"jmiller@gmail.com\",\"name\":\"Joe Miller\"}"));
    }

    @Test
    public void test_getInvalidUser_throwsException() throws Exception {
        when(userRepository.get(12))
                .thenReturn(Optional.empty());

        mvc.perform(get("/profile")
                .requestAttr("userId", 12)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Invalid user id.\"}"));
    }

}
