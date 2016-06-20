package com.tokyo.beach.comment;

import com.tokyo.beach.restaurants.comment.*;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import com.tokyo.beach.user.UserFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CommentControllerTest {
    CommentDataMapper commentDataMapper;
    UserDataMapper userDataMapper;
    CommentController commentController;
    CommentRepository commentRepository;
    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        commentDataMapper = mock(CommentDataMapper.class);
        userDataMapper = mock(UserDataMapper.class);
        commentRepository = mock(CommentRepository.class);
        commentController = new CommentController(commentRepository, commentDataMapper, userDataMapper);
        mockMvc = standaloneSetup(commentController).build();
    }

    @Test
    public void test_create_returnsCreatedHTTPStatus() throws Exception {
        when(userDataMapper.get(anyLong()))
                .thenReturn(
                        Optional.of(
                                new UserFixture().build()
                        )
                );
        when(commentDataMapper.create(
                anyObject(),
                anyLong(),
                anyLong()
        )).thenReturn(
                new CommentFixture().build()
        );


        ResultActions result = mockMvc.perform(post("/restaurants/88/comments")
                .requestAttr("userId", 11L)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"comment\":\"New Comment Text\"}")
        );


        result.andExpect(status().isCreated());
    }

    @Test
    public void test_create_createsComment() throws Exception {
        ArgumentCaptor<NewComment> attributeNewComment = ArgumentCaptor.forClass(NewComment.class);
        ArgumentCaptor<Long> attributeCreatedByUserId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> attributeRestaurantId = ArgumentCaptor.forClass(Long.class);
        when(userDataMapper.get(99))
                .thenReturn(Optional.of(
                        new User(
                                99,
                                "user-email",
                                "user-name"
                        ))
                );
        when(commentDataMapper.create(
                attributeNewComment.capture(),
                attributeCreatedByUserId.capture(),
                attributeRestaurantId.capture()
        )).thenReturn(
                new Comment(
                        1,
                        "New Comment Text",
                        "2016-02-29 06:07:55.000000",
                        88,
                        99
                )
        );


        ResultActions result = mockMvc.perform(post("/restaurants/88/comments")
                .requestAttr("userId", 99)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"comment\":\"New Comment Text\"}"));


        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.comment", is("New Comment Text")))
                .andExpect(jsonPath("$.created_at", is("2016-02-29T06:07:55.000Z")))
                .andExpect(jsonPath("$.restaurant_id", is(88)))
                .andExpect(jsonPath("$.user.name", is("user-name")));

        assertEquals(new NewComment("New Comment Text"), attributeNewComment.getValue());
        assertEquals(99, attributeCreatedByUserId.getValue().longValue());
        assertEquals(88, attributeRestaurantId.getValue().longValue());
    }

    @Test
    public void test_delete_returnsOkHTTPStatus() throws Exception {
        when(commentDataMapper.get(
                anyLong()
        )).thenReturn(Optional.empty());


        ResultActions result = mockMvc.perform(delete("/comments/88")
                .requestAttr("userId", 11L)
        );


        result.andExpect(status().isOk());
    }

    @Test
    public void test_delete_deletesCommentsMadeByCurrentUser() throws Exception {
        when(commentDataMapper.get(1))
                .thenReturn(Optional.of(
                        new Comment(
                                1,
                                "comment",
                                "2016-02-29 06:07:55.000000",
                                10L,
                                99L
                        )
                ));
        when(userDataMapper.get(99))
                .thenReturn(Optional.of(
                        new User(
                                99,
                                "user-email",
                                "user-name"
                        ))
                );
        ResultActions result = mockMvc.perform(delete("/comments/1")
                .requestAttr("userId", 99));

        result.andExpect(status().isOk());
        verify(commentDataMapper, times(1)).get(1);
        verify(commentDataMapper, times(1)).delete(1);
    }

    @Test
    public void test_delete_doesntDeleteCommentsMadeByADifferentUser() throws Exception {
        when(commentDataMapper.get(1))
                .thenReturn(Optional.of(
                        new Comment(
                                1,
                                "comment",
                                "2016-02-29 06:07:55.000000",
                                10L,
                                100L
                        )
                ));
        when(userDataMapper.get(99))
                .thenReturn(Optional.of(
                        new User(
                                99,
                                "user-email",
                                "user-name"
                        ))
                );
        ResultActions result = mockMvc.perform(delete("/comments/1")
                .requestAttr("userId", 99));

        result.andExpect(status().isOk());
        verify(commentDataMapper, times(1)).get(1);
        verify(commentDataMapper, never()).delete(1);
    }

    @Test
    public void test_delete_doesntDeleteNonExistentComment() throws Exception {
        when(commentDataMapper.get(1))
                .thenReturn(Optional.empty()
                );
        when(userDataMapper.get(99))
                .thenReturn(Optional.of(
                        new User(
                                99,
                                "user-email",
                                "user-name"
                        ))
                );
        ResultActions result = mockMvc.perform(delete("/comments/1")
                .requestAttr("userId", 99));

        result.andExpect(status().isOk());
        verify(commentDataMapper, times(1)).get(1);
        verify(commentDataMapper, never()).delete(1);
    }



    @Test
    public void test_get_returnsCommentsForARestaurant() throws Exception {
        when(commentRepository.findForRestaurant(1L))
                .thenReturn(Arrays.asList(
                        new SerializedComment(
                                new Comment(1L, "this is a comment", "2016-02-29 06:07:55.000000", 1L, 10L),
                                new User(10L, "danny@mail", "Danny")
                        )
                ));
        ResultActions result = mockMvc.perform(get("/restaurants/1/comments")
                .requestAttr("userId", 99));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].comment", is("this is a comment")))
                .andExpect(jsonPath("$[0].created_at", is("2016-02-29T06:07:55.000Z")))
                .andExpect(jsonPath("$[0].restaurant_id", is(1)))
                .andExpect(jsonPath("$[0].user.name", is("Danny")));
        verify(commentRepository, times(1)).findForRestaurant(1L);
    }
}
