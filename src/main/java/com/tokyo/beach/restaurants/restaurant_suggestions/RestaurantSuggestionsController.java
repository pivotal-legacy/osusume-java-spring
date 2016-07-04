package com.tokyo.beach.restaurants.restaurant_suggestions;

import okhttp3.HttpUrl;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@CrossOrigin
@RestController
public class RestaurantSuggestionsController {
    private RestaurantSuggestionRepository restaurantSuggestionRepository;

    public RestaurantSuggestionsController() {
        this.restaurantSuggestionRepository = new RestaurantSuggestionRepository();
    }

    public RestaurantSuggestionsController(RestaurantSuggestionRepository restaurantSuggestionRepository) {
        this.restaurantSuggestionRepository = restaurantSuggestionRepository;
    }

    @RequestMapping(value = "/restaurant_suggestions", method = POST)
    public List<RestaurantSuggestion> getAll(@RequestBody RestaurantSuggestionParams params) {
        String baseUrl = "https://maps.googleapis.com";
        String path = "/maps/api/place/textsearch/json";
        String key = "?key=" + System.getenv("GOOGLE_PLACES_KEY");
        String query = "&query=" + params.getRestaurantName();
        HttpUrl url = HttpUrl.parse(baseUrl + path + key + query);
        return restaurantSuggestionRepository.getAll(url);
    }
}
