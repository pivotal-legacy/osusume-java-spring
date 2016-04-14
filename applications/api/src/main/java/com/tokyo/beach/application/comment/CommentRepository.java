package com.tokyo.beach.application.comment;

import com.tokyo.beach.application.restaurant.Restaurant;
import java.util.List;

public interface CommentRepository {
    Comment create(NewComment newComment, long createdByUserId, String restaurantId);
    List<SerializedComment> findForRestaurant(Restaurant restaurant);
}
