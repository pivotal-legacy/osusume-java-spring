package com.tokyo.beach.restaurants.comment;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment create(NewComment newComment, long createdByUserId, String restaurantId);
    List<SerializedComment> findForRestaurant(long restaurantId);
    Optional<Comment> get(long commentId);
    void delete(long commentId);
}
