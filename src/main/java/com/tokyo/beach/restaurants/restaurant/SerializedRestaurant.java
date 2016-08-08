package com.tokyo.beach.restaurants.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.user.User;

import java.util.List;

import static com.tokyo.beach.restaurants.DateFormatter.formatDateForSerialization;
import static java.util.Collections.emptyList;

public class SerializedRestaurant {
    private Restaurant restaurant;
    private List<PhotoUrl> photoUrls;
    private Cuisine cuisine;
    private PriceRange priceRange;
    private User createdByUser;
    private List<SerializedComment> comments;
    private boolean currentUserLikesRestaurant;
    private long numberOfLikes;

    public SerializedRestaurant(
            Restaurant restaurant,
            List<PhotoUrl> photoUrls,
            Cuisine cuisine,
            PriceRange priceRange,
            User createdByUser,
            List<SerializedComment> comments,
            boolean currentUserLikesRestaurant,
            long numberOfLikes) {
        this.restaurant = restaurant;
        this.photoUrls = photoUrls;
        this.cuisine = cuisine;
        this.priceRange = priceRange;
        this.createdByUser = createdByUser;
        this.comments = comments;
        this.currentUserLikesRestaurant = currentUserLikesRestaurant;
        this.numberOfLikes = numberOfLikes;
    }

    public long getId() {
        return restaurant.getId();
    }

    public String getName() {
        return restaurant.getName();
    }

    @JsonProperty("user")
    public User getCreatedByUser() {
        return createdByUser;
    }

    @SuppressWarnings("unused")
    public String getAddress() {
        return restaurant.getAddress();
    }

    @SuppressWarnings("unused")
    @JsonProperty("nearest_station")
    public String getNearestStation() {
        return restaurant.getNearestStation();
    }

    @JsonProperty("place_id")
    public String getPlaceId() {
        return restaurant.getPlaceId();
    }

    @JsonProperty("latitude")
    public double getLatitude() {
        return restaurant.getLatitude();
    }

    @JsonProperty("longitude")
    public double getLongitude() {
        return restaurant.getLongitude();
    }

    @SuppressWarnings("unused")
    public String getNotes() {
        return restaurant.getNotes();
    }

    @JsonProperty("created_by_user_name")
    public String getCreatedByUserName() {
        return createdByUser.getName();
    }

    @JsonProperty("created_at")
    public String getCreatedDate() {
        return formatDateForSerialization(restaurant.getCreatedDate());
    }

    @JsonProperty("updated_at")
    public String getUpdatedDate() {
        return formatDateForSerialization(restaurant.getUpdatedDate());
    }

    @JsonProperty("photo_urls")
    public List<PhotoUrl> getPhotoUrlList() {
        if (photoUrls == null) {
            return emptyList();
        } else {
            return photoUrls;
        }
    }

    @JsonProperty("comments")
    public List<SerializedComment> getComments() {
        return comments;
    }

    @JsonProperty("cuisine")
    public Cuisine getCuisine() {
        return cuisine;
    }

    @JsonProperty("price_range")
    public PriceRange getPriceRange() { return priceRange; }

    @JsonProperty("liked")
    public boolean isCurrentUserLikesRestaurant() {
        return currentUserLikesRestaurant;
    }

    @JsonProperty("num_likes")
    public long getNumberOfLikes() {
        return numberOfLikes;
    }
}
