package com.tokyo.beach.restaurants.restaurant_suggestions;

import okhttp3.HttpUrl;
import org.springframework.context.annotation.PropertySource;
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
        HttpUrl url = HttpUrl.parse("http://api.gnavi.co.jp/RestSearchAPI/20150630/?keyid=" + System.getenv("GNAVI_KEY") + "&format=json&name=" + params.getRestaurantName());
        return restaurantSuggestionRepository.getAll(url);
    }
}
