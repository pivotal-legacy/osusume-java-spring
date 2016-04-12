package com.tokyo.beach.application.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.application.user.DatabaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        String result = "";
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            Date date = format.parse(comment.getCreatedDate());
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            result = format.format(date);
        } catch (ParseException e) {
        }
        return result;
    }

    @JsonProperty("restaurant_id")
    public long getRestaurantId() {
        return comment.getRestaurantId();
    }

    public DatabaseUser getUser() {
        return user;
    }
}
