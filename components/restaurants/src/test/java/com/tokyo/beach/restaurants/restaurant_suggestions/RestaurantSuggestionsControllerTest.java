package com.tokyo.beach.restaurants.restaurant_suggestions;

import com.tokyo.beach.restutils.RestControllerExceptionHandler;
import okhttp3.HttpUrl;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.tokyo.beach.restutils.ControllerTestingUtils.createControllerAdvice;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class RestaurantSuggestionsControllerTest {
    @Test
    public void getAllRestaurantSuggestionsTest() throws Exception {
        RestaurantSuggestionRepository restaurantSuggestionRepository = mock(RestaurantSuggestionRepository.class);
        RestaurantSuggestionsController restaurantSuggestionsController
                = new RestaurantSuggestionsController(restaurantSuggestionRepository);
        MockMvc mockMvc = standaloneSetup(restaurantSuggestionsController)
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();

        List<RestaurantSuggestion> suggestions = singletonList(
                new RestaurantSuggestion("place-id", "Afuri", "Roppongi")
        );

        ArgumentCaptor<HttpUrl> urlArgument = ArgumentCaptor.forClass(HttpUrl.class);
        when(restaurantSuggestionRepository.getAll(urlArgument.capture()))
                .thenReturn(suggestions);

        String searchQuery = "Afuri Roppongi";
        String baseUrl = "https://maps.googleapis.com";
        String path = "/maps/api/place/textsearch/json";
        String key = "?key=" + System.getenv("GOOGLE_PLACES_KEY");
        String query = "&query=" + searchQuery;
        HttpUrl url = HttpUrl.parse(baseUrl + path + key + query);

        String payload = "{\"restaurantName\":\"" + searchQuery + "\"}";
        mockMvc.perform(
                post("/restaurant_suggestions")
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .content(payload)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].place_id", equalTo("place-id")));

        assertEquals(url, urlArgument.getValue());
    }
}
