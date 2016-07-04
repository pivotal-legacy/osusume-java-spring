package com.tokyo.beach.restaurants.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {
    private long id;
    private String comment;
    private String createdDate;
    private long restaurantId;
    private long createdByUserId;

    public Comment(long id,
                   String comment,
                   String createdDate,
                   long restaurantId,
                   long createdByUserId) {
        this.id = id;
        this.comment = comment;
        this.createdDate = createdDate;
        this.restaurantId = restaurantId;
        this.createdByUserId = createdByUserId;
    }

    public long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getComment() {
        return comment;
    }

    @SuppressWarnings("WeakerAccess")
    @JsonProperty("created_at")
    public String getCreatedDate() {
        return createdDate;
    }

    @JsonProperty("restaurant_id")
    public long getRestaurantId() {
        return restaurantId;
    }

    @SuppressWarnings("unused")
    public long getCreatedByUserId() {
        return createdByUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (id != comment.id) return false;
        if (restaurantId != comment.restaurantId) return false;
        if (createdByUserId != comment.createdByUserId) return false;
        //noinspection SimplifiableIfStatement
        if (this.comment != null ? !this.comment.equals(comment.comment) : comment.comment != null) return false;
        return createdDate != null ? createdDate.equals(comment.createdDate) : comment.createdDate == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (int) (restaurantId ^ (restaurantId >>> 32));
        result = 31 * result + (int) (createdByUserId ^ (createdByUserId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", restaurantId=" + restaurantId +
                ", createdByUserId=" + createdByUserId +
                '}';
    }
}
