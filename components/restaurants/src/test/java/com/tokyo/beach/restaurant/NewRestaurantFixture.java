package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;

import java.util.List;

public class NewRestaurantFixture {
    private Long priceRangeId = 0L;
    private long cuisineId = 0;
    private String name = "Not Specified";
    private String address = "address";
    private List<NewPhotoUrl> photoUrls = null;
    private String notes = "notes";

    public NewRestaurantFixture withPriceRangeId(Long priceRangeId) {
        this.priceRangeId = priceRangeId;
        return this;
    }

    public NewRestaurantFixture withCuisineId(long cuisineId) {
        this.cuisineId = cuisineId;
        return this;
    }

    public NewRestaurantFixture withName(String name) {
        this.name = name;
        return this;
    }

    public NewRestaurantFixture withNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public NewRestaurantFixture withAddress(String address) {
        this.address = address;
        return this;
    }

    public NewRestaurantFixture withPhotoUrls(List<NewPhotoUrl> photoUrls) {
        this.photoUrls = photoUrls;
        return this;
    }

    public NewRestaurant build() {
        return new NewRestaurant(
                name,
                address,
                notes,
                cuisineId,
                priceRangeId,
                photoUrls
        );
    }
}
