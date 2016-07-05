package com.tokyo.beach.restaurants.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Restaurant {
    private final long id;
    private final String name;
    private String address;
    private String placeId;
    private Double latitude;
    private Double longitude;
    private String notes;
    private String createdDate;
    private String updatedDate;
    private long createdByUserId;
    private long priceRangeId;
    private long cuisineId;

    public Restaurant(
            long id,
            String name,
            String address,
            String placeId,
            double latitude,
            double longitude,
            String notes,
            String createdDate,
            String updatedDate,
            long createdByUserId,
            long priceRangeId,
            long cuisineId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
        this.createdDate = createdDate;
        this.createdByUserId = createdByUserId;
        this.priceRangeId = priceRangeId;
        this.cuisineId = cuisineId;
        this.updatedDate = updatedDate;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public String getAddress() {
        return address;
    }

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

    public long getCreatedByUserId() {
        return createdByUserId;
    }

    @JsonProperty("created_at")
    public String getCreatedDate() {
        return createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public long getPriceRangeId() {
        return priceRangeId;
    }

    public Long getCuisineId() {
        return cuisineId;
    }
}
