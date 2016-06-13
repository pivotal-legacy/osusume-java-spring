package com.tokyo.beach.comment;

import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.comment.*;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserRepository;
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
    CommentRepository mockCommentRepository;
    UserRepository mockUserRepository;
    CommentController commentController;
    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockCommentRepository = mock(CommentRepository.class);
        mockUserRepository = mock(UserRepository.class);
        commentController = new CommentController(mockCommentRepository, mockUserRepository);
        mockMvc = standaloneSetup(commentController).build();
    }

    @Test
    public void test_create_returnsCreatedHTTPStatus() throws Exception {
        when(mockUserRepository.get(anyLong()))
                .thenReturn(
                        Optional.of(
                                new UserFixture().build()
                        )
                );
        when(mockCommentRepository.create(
                anyObject(),
                anyLong(),
                anyString()
        )).thenReturn(
                new CommentFixture().build()
        );


        ResultActions result = mockMvc.perform(post("/restaurants/88/comments")
                .requestAttr("userId", 11L)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content("{\"comment\":{\"content\":\"New Comment Text\"}}")
        );


        result.andExpect(status().isCreated());
    }

    @Test
    public void test_create_createsComment() throws Exception {
        ArgumentCaptor<NewComment> attributeNewComment = ArgumentCaptor.forClass(NewComment.class);
        ArgumentCaptor<Long> attributeCreatedByUserId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> attributeRestaurantId = ArgumentCaptor.forClass(String.class);
        when(mockUserRepository.get(99))
                .thenReturn(Optional.of(
                        new User(
                                99,
                                "user-email",
                                "user-name"
                        ))
                );
        when(mockCommentRepository.create(
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
                .content("{\"comment\":{\"content\":\"New Comment Text\"}}"));


        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("New Comment Text")))
                .andExpect(jsonPath("$.created_at", is("2016-02-29T06:07:55.000Z")))
                .andExpect(jsonPath("$.restaurant_id", is(88)))
                .andExpect(jsonPath("$.user.name", is("user-name")));

        assertEquals(99, attributeCreatedByUserId.getValue().longValue());
        assertEquals("88", attributeRestaurantId.getValue());
    }

    @Test
    public void test_delete_returnsOkHTTPStatus() throws Exception {
        when(mockCommentRepository.get(
                anyLong()
        )).thenReturn(Optional.empty());


        ResultActions result = mockMvc.perform(delete("/comments/88")
                .requestAttr("userId", 11L)
        );


        result.andExpect(status().isOk());
    }

    @Test
    public void test_delete_deletesCommentsMadeByCurrentUser() throws Exception {
        when(mockCommentRepository.get(1))
                .thenReturn(Optional.of(
                        new Comment(
                                1,
                                "comment",
                                "2016-02-29 06:07:55.000000",
                                10L,
                                99L
                        )
                ));
        when(mockUserRepository.get(99))
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
        verify(mockCommentRepository, times(1)).get(1);
        verify(mockCommentRepository, times(1)).delete(1);
    }

    @Test
    public void test_delete_doesntDeleteCommentsMadeByADifferentUser() throws Exception {
        when(mockCommentRepository.get(1))
                .thenReturn(Optional.of(
                        new Comment(
                                1,
                                "comment",
                                "2016-02-29 06:07:55.000000",
                                10L,
                                100L
                        )
                ));
        when(mockUserRepository.get(99))
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
        verify(mockCommentRepository, times(1)).get(1);
        verify(mockCommentRepository, never()).delete(1);
    }

    @Test
    public void test_delete_doesntDeleteNonExistentComment() throws Exception {
        when(mockCommentRepository.get(1))
                .thenReturn(Optional.empty()
                );
        when(mockUserRepository.get(99))
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
        verify(mockCommentRepository, times(1)).get(1);
        verify(mockCommentRepository, never()).delete(1);
    }



    @Test
    public void test_get_returnsCommentsForARestaurant() throws Exception {
        when(mockCommentRepository.findForRestaurant(1L))
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
                .andExpect(jsonPath("$[0].content", is("this is a comment")))
                .andExpect(jsonPath("$[0].comment", is("this is a comment")))
                .andExpect(jsonPath("$[0].created_at", is("2016-02-29T06:07:55.000Z")))
                .andExpect(jsonPath("$[0].restaurant_id", is(1)))
                .andExpect(jsonPath("$[0].user.name", is("Danny")));
        verify(mockCommentRepository, times(1)).findForRestaurant(1L);
    }
}
