package com.tokyo.beach.comment;

import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.user.UserFixture;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializedCommentTest {

    @Test
    public void test_getFormattedCreatedDate_returnsTheFormattedDateString() {
        Comment comment = new CommentFixture().build();
        User user = new UserFixture().build();
        SerializedComment serializedComment = new SerializedComment(comment, user);

        assertEquals(serializedComment.getFormattedCreatedDate(), "1970-01-01T00:00:00.000Z");
    }
}
