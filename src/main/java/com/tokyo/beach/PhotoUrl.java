package com.tokyo.beach;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")
public class PhotoUrl {
    @JsonIgnore
    private int id;

    private String url;

    @JsonIgnore
    private int restaurantId;

    public PhotoUrl() {}

    public PhotoUrl(int id, String url, int restaurantId) {
        this.id = id;
        this.url = url;
        this.restaurantId = restaurantId;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass()) return false;

        PhotoUrl photoUrl = (PhotoUrl)o;
        return id == photoUrl.id &&
                (url != null ? url.equals(photoUrl.url) : photoUrl.url == null) &&
                restaurantId == photoUrl.restaurantId;
    }

    public String toString() {
        return "id: " + id + ",\n" +
                "url: " + url + ",\n" +
                "restaurant_id: " + restaurantId;
    }
}
