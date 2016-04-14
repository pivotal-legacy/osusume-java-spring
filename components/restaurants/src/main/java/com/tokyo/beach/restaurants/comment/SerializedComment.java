package com.tokyo.beach.restaurants.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.restaurants.user.DatabaseUser;

import static com.tokyo.beach.restaurants.DateFormatter.formatDateForSerialization;

public class SerializedComment {
    private Comment comment;
    private DatabaseUser user;

    @SuppressWarnings("unused")
    public SerializedComment(Comment comment, DatabaseUser user) {
        this.comment = comment;
        this.user = user;
    }

    public long getId() {
        return comment.getId();
    }

    @SuppressWarnings("unused")
    public String getContent() {
        return comment.getContent();
    }

    @JsonProperty("created_at")
    public String getCreatedDate() {
        return formatDateForSerialization(comment.getCreatedDate());
    }

    @JsonProperty("restaurant_id")
    public long getRestaurantId() {
        return comment.getRestaurantId();
    }

    public DatabaseUser getUser() {
        return user;
    }
}
