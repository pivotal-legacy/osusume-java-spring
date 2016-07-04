package com.tokyo.beach.restaurants.restaurant_suggestions;

public class RestaurantSuggestionParams {
    private String restaurantName;

    public RestaurantSuggestionParams() {

    }

    public RestaurantSuggestionParams(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantSuggestionParams that = (RestaurantSuggestionParams) o;

        return restaurantName != null ? restaurantName.equals(that.restaurantName) : that.restaurantName == null;
    }
}
