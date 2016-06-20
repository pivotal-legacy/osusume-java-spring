package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;

import java.util.List;

public class NewRestaurantFixture {
    private Long priceRangeId = 0L;
    private long cuisineId = 0;
    private Restaurant restaurant = null;
    private List<NewPhotoUrl> photoUrls = null;


    public NewRestaurantFixture withPriceRangeId(Long priceRangeId) {
        this.priceRangeId = priceRangeId;
        return this;
    }

    public NewRestaurantFixture withCuisineId(long cuisineId) {
        this.cuisineId = cuisineId;
        return this;
    }


    public NewRestaurantFixture withRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        return this;
    }

    public NewRestaurantFixture withPhotoUrls(List<NewPhotoUrl> photoUrls) {
        this.photoUrls = photoUrls;
        return this;
    }

    public NewRestaurant build() {
        return new NewRestaurant(
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getOffersEnglishMenu(),
                restaurant.getWalkInsOk(),
                restaurant.getAcceptsCreditCards(),
                restaurant.getNotes(),
                cuisineId,
                priceRangeId,
                photoUrls
        );
    }
}
