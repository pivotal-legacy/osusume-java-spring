package com.tokyo.beach.restaurants.photos;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PhotoUrl {
    private long id;

    private String url;

    @JsonIgnore
    private long restaurantId;

    @SuppressWarnings("unused")
    public PhotoUrl() {}

    public PhotoUrl(long id, String url, long restaurantId) {
        this.id = id;
        this.url = url;
        this.restaurantId = restaurantId;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoUrl photoUrl = (PhotoUrl) o;

        if (id != photoUrl.id) return false;
        if (restaurantId != photoUrl.restaurantId) return false;
        return url != null ? url.equals(photoUrl.url) : photoUrl.url == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (int) (restaurantId ^ (restaurantId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PhotoUrl{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", restaurantId=" + restaurantId +
                '}';
    }
}
