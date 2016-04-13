package com.tokyo.beach.application.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.application.comment.SerializedComment;
import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.application.user.DatabaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SerializedRestaurant {
    private Restaurant restaurant;
    private List<PhotoUrl> photoUrls;
    private Cuisine cuisine;
    private Optional<DatabaseUser> createdByUser;
    private List<SerializedComment> comments;

    public SerializedRestaurant(
            Restaurant restaurant,
            List<PhotoUrl> photoUrls,
            Cuisine cuisine,
            Optional<DatabaseUser> createdByUser,
            List<SerializedComment> comments
    ) {
        this.restaurant = restaurant;
        this.photoUrls = photoUrls;
        this.cuisine = cuisine;
        this.createdByUser = createdByUser;
        this.comments = comments;
    }

    public long getId() {
        return restaurant.getId();
    }

    public String getName() {
        return restaurant.getName();
    }

    @JsonProperty("user")
    public DatabaseUser getCreatedByUser() {
        return createdByUser.orElse(null);
    }

    @SuppressWarnings("unused")
    public String getAddress() {
        return restaurant.getAddress();
    }

    @JsonProperty("offers_english_menu")
    public Boolean getOffersEnglishMenu() {
        return restaurant.getOffersEnglishMenu();
    }

    @JsonProperty("walk_ins_ok")
    public Boolean getWalkInsOk() {
        return restaurant.getWalkInsOk();
    }

    @JsonProperty("accepts_credit_cards")
    public Boolean getAcceptsCreditCards() {
        return restaurant.getAcceptsCreditCards();
    }

    @SuppressWarnings("unused")
    public String getNotes() {
        return restaurant.getNotes();
    }


    @JsonProperty("created_by_user_name")
    public String getCreatedByUserName() {
        String username = "";

        if (createdByUser.isPresent()) {
            username = createdByUser.get().getName();
        }
        return username;
    }

    @JsonProperty("created_at")
    public String getCreatedDate() {
        String result = "";
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            Date date = format.parse(restaurant.getCreatedDate());
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            result = format.format(date);
        } catch (ParseException e) {
        }
        return result;
    }

    @JsonProperty("photo_urls")
    public List<PhotoUrl> getPhotoUrlList() {
        return photoUrls;
    }

    @JsonProperty("comments")
    public List<SerializedComment> getComments() {
        return comments;
    }

    public Cuisine getCuisine() {
        return cuisine;
    }

    @Override
    public String toString() {
        return "SerializedRestaurant{" +
                "restaurant=" + restaurant +
                ", photoUrls=" + photoUrls +
                ", cuisine=" + cuisine +
                ", createdByUser=" + createdByUser +
                '}';
    }
}
