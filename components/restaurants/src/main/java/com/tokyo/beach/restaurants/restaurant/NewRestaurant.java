package com.tokyo.beach.restaurants.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;

import java.util.List;

@SuppressWarnings("unused")
public class NewRestaurant {

    private String name;
    private String address;
    private String notes;

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
                         String notes,
                         Long cuisineId,
                         Long priceRangeId,
                         List<NewPhotoUrl> photoUrls)
    {
        this.name = name;
        this.address = address;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewRestaurant that = (NewRestaurant) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (photoUrls != null ? !photoUrls.equals(that.photoUrls) : that.photoUrls != null) return false;
        //noinspection SimplifiableIfStatement
        if (cuisineId != null ? !cuisineId.equals(that.cuisineId) : that.cuisineId != null) return false;
        return priceRangeId != null ? priceRangeId.equals(that.priceRangeId) : that.priceRangeId == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (photoUrls != null ? photoUrls.hashCode() : 0);
        result = 31 * result + (cuisineId != null ? cuisineId.hashCode() : 0);
        result = 31 * result + (priceRangeId != null ? priceRangeId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NewRestaurant{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", notes='" + notes + '\'' +
                ", photoUrls=" + photoUrls +
                ", cuisineId=" + cuisineId +
                ", priceRangeId=" + priceRangeId +
                '}';
    }
}
