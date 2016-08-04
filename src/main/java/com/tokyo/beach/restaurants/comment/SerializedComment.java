package com.tokyo.beach.restaurants.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.restaurants.user.User;

import static com.tokyo.beach.restaurants.DateFormatter.formatDateForSerialization;

public class SerializedComment {
    private Comment comment;
    private User user;

    @SuppressWarnings("unused")
    public SerializedComment(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
    }

    public long getId() {
        return comment.getId();
    }

    public String getComment() {
        return comment.getComment();
    }

    @JsonProperty("created_at")
    public String getFormattedCreatedDate() {
        return formatDateForSerialization(comment.getCreatedDate());
    }

    @JsonProperty("restaurant_id")
    public long getRestaurantId() {
        return comment.getRestaurantId();
    }

    public User getUser() {
        return user;
    }
}
