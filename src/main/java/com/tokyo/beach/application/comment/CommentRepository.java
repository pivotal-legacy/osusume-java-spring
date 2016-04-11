package com.tokyo.beach.application.comment;

public interface CommentRepository {
    Comment create(NewComment newComment, long createdByUserId, String restaurantId);
}
