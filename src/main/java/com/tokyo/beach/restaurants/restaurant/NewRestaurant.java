package com.tokyo.beach.restaurants.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;

import java.util.List;

@SuppressWarnings("unused")
public class NewRestaurant {

    private String name;
    private String address;
    private String notes;

    @JsonProperty("place_id")
    private String placeId;

    private double latitude;
    private double longitude;

    @JsonProperty("photo_urls")
    private List<NewPhotoUrl> photoUrls;

    @JsonProperty("cuisine_id")
    private Long cuisineId;

    @JsonProperty("price_range_id")
    private Long priceRangeId;

    public NewRestaurant() {
    }

    public NewRestaurant(String name,
                         String address,
                         String placeId,
                         double latitude,
                         double longitude,
                         String notes,
                         Long cuisineId,
                         Long priceRangeId,
                         List<NewPhotoUrl> photoUrls)
    {
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
        this.cuisineId = cuisineId;
        this.priceRangeId = priceRangeId;
        this.photoUrls = photoUrls;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public String getAddress() {
        return address;
    }

    @JsonProperty("place_id")
    public String getPlaceId() {
        return placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @SuppressWarnings("WeakerAccess")
    public String getNotes() {
        return notes;
    }

    @SuppressWarnings("WeakerAccess")
    public List<NewPhotoUrl> getPhotoUrls() {
        return photoUrls;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getCuisineId() {
        if (cuisineId != null) {
            return cuisineId;
        } else {
            return 0L;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public Long getPriceRangeId() {
        return priceRangeId;
    }

}
