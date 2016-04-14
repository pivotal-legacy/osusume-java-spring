package com.tokyo.beach.application.like;

public class Like {
    private long userId;

    private long restaurantId;
    @SuppressWarnings("unused")
    public Like(long userId, long restaurantId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public long getUserId() {
        return userId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Like like = (Like) o;

        if (userId != like.userId) return false;
        return restaurantId == like.restaurantId;

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (int) (restaurantId ^ (restaurantId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Like{" +
                "userId=" + userId +
                ", restaurantId=" + restaurantId +
                '}';
    }

}
