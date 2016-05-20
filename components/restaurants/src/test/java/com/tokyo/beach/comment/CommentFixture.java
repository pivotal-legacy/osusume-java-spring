package com.tokyo.beach.comment;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurant.NewRestaurantFixture;
import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.comment.NewComment;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import org.springframework.jdbc.core.JdbcTemplate;

public class CommentFixture {
    private long id = 0;
    private String content = "content";
    private String createdDate = "created-date";
    private long restaurantId = 0;
    private long createdByUserId = 0;

    public CommentFixture withId(long id) {
        this.id = id;
        return this;
    }

    public CommentFixture withContent(String content) {
        this.content = content;
        return this;
    }

    public CommentFixture withCreatedDate(String createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public CommentFixture withRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public CommentFixture withCreatedByUserId(long createdByUserId) {
        this.createdByUserId = createdByUserId;
        return this;
    }

    public Comment build() {
        return new Comment(
                id,
                content,
                createdDate,
                restaurantId,
                createdByUserId
        );
    }

    public Comment persist(JdbcTemplate jdbcTemplate) {

        NewComment newComment = new NewComment(content);

        return TestDatabaseUtils.insertCommentIntoDatabase(
                jdbcTemplate,
                newComment,
                createdByUserId,
                restaurantId
        );
    }
}
