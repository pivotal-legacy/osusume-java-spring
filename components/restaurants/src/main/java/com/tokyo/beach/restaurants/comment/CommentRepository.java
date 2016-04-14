package com.tokyo.beach.restaurants.comment;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import java.util.List;

public interface CommentRepository {
    Comment create(NewComment newComment, long createdByUserId, String restaurantId);
    List<SerializedComment> findForRestaurant(Restaurant restaurant);
}
