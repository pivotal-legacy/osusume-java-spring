package com.tokyo.beach.application.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.photos.PhotoUrl;

import java.util.List;

class SerializedRestaurant {
    private Restaurant restaurant;
    private List<PhotoUrl> photoUrls;
    private Cuisine cuisine;


    SerializedRestaurant(Restaurant restaurant, List<PhotoUrl> photoUrls, Cuisine cuisine) {
        this.restaurant = restaurant;
        this.photoUrls = photoUrls;
        this.cuisine = cuisine;
    }

    public long getId() {
        return restaurant.getId();
    }

    public String getName() {
        return restaurant.getName();
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

    @JsonProperty("photo_urls")
    public List<PhotoUrl> getPhotoUrlList() {
        return photoUrls;
    }

    public Cuisine getCuisine() {
        return cuisine;
    }
}
