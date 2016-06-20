package com.tokyo.beach.comment;

import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.comment.CommentDataMapper;
import com.tokyo.beach.restaurants.comment.NewComment;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.NewUser;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.user.UserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CommentDataMapperTest {
    JdbcTemplate jdbcTemplate;
    CommentDataMapper commentDataMapper;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        commentDataMapper = new CommentDataMapper(jdbcTemplate);
        createDefaultCuisine(jdbcTemplate);
        createDefaultPriceRange(jdbcTemplate);
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_create_createsAComment() throws Exception {
        User user = new UserFixture().persist(jdbcTemplate);

        Restaurant restaurant = new RestaurantFixture().withUser(user).persist(jdbcTemplate);

        Comment createdComment = commentDataMapper.create(
                new NewComment("New Comment Content"),
                user.getId(),
                restaurant.getId()
        );

        Comment actualComment = jdbcTemplate.queryForObject(
                "SELECT * FROM comment WHERE id=?",
                (rs, rowNum) -> {
                    return new Comment(
                            rs.getLong("id"),
                            rs.getString("content"),
                            rs.getString("created_at"),
                            rs.getLong("restaurant_id"),
                            rs.getLong("created_by_user_id")
                    );
                },
                createdComment.getId()
        );

        assertThat(actualComment.getComment(), is("New Comment Content"));
        assertThat(actualComment.getCreatedByUserId(), is(user.getId()));
        assertThat(actualComment.getRestaurantId(), is(restaurant.getId()));
    }

    @Test
    public void test_findForRestaurant_returnsCommentsOnRestaurant() throws Exception {
        User user = new UserFixture().persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture().withUser(user).persist(jdbcTemplate);

        Comment createdComment = commentDataMapper.create(
                new NewComment("New Comment Content"),
                user.getId(),
                restaurant.getId()
        );

        List<SerializedComment> actualComments = commentDataMapper.findForRestaurant(restaurant.getId());
        assertEquals(actualComments.size(), 1);
        assertEquals(actualComments.get(0).getComment(), createdComment.getComment());
        assertEquals(actualComments.get(0).getUser().getId(), createdComment.getCreatedByUserId());
        assertEquals(actualComments.get(0).getRestaurantId(), createdComment.getRestaurantId());
    }

    @Test
    public void test_delete_deletesComment() throws Exception {
        User user = new UserFixture().withEmail("email1").persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture()
                .withUser(user)
                .persist(jdbcTemplate);
        Comment comment = new CommentFixture()
                .withContent("content")
                .withCreatedByUserId(user.getId())
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);

        commentDataMapper.delete(comment.getId());

        int count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM comment WHERE id = ?",
                new Object[]{comment.getId()},
                Integer.class
        );

        assertEquals(0, count);
    }

    @Test
    public void test_get_returnsCommentMatchingId() throws Exception {
        User user = new UserFixture().withEmail("email1").persist(jdbcTemplate);
        Restaurant restaurant = new RestaurantFixture()
                .withUser(user)
                .persist(jdbcTemplate);
        Comment persistedComment = new CommentFixture()
                .withContent("content")
                .withCreatedByUserId(user.getId())
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);

        Comment retrievedComment = commentDataMapper.get(persistedComment.getId()).get();


        assertEquals(persistedComment, retrievedComment);
    }

    @Test
    public void test_get_returnsEmptyOptionalWhenCommentDoesntExistWithId() throws Exception {
        Optional<Comment> retrievedComment = commentDataMapper.get(991);

        assertEquals(retrievedComment, Optional.empty());
    }
}
