package com.tokyo.beach.restaurants.restaurant_suggestions;

import java.util.List;

public class GooglePlacesResult {
    private List<RestaurantSuggestion> results;

    public GooglePlacesResult() {}

    public List<RestaurantSuggestion> getResults() {
        return results;
    }
}
