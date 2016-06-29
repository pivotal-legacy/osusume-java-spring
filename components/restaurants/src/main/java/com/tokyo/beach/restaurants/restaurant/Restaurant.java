package com.tokyo.beach.restaurants.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Restaurant {
    private final long id;
    private final String name;
    private String address;
    private String placeId;
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

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", notes='" + notes + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", createdByUserId=" + createdByUserId +
                ", priceRangeId=" + priceRangeId +
                ", cuisineId=" + cuisineId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Restaurant that = (Restaurant) o;

        if (id != that.id) return false;
        if (createdByUserId != that.createdByUserId) return false;
        if (priceRangeId != that.priceRangeId) return false;
        if (cuisineId != that.cuisineId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        //noinspection SimplifiableIfStatement
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        return updatedDate != null ? updatedDate.equals(that.updatedDate) : that.updatedDate == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (int) (createdByUserId ^ (createdByUserId >>> 32));
        result = 31 * result + (int) (priceRangeId ^ (priceRangeId >>> 32));
        result = 31 * result + (int) (cuisineId ^ (cuisineId >>> 32));
        return result;
    }
}
