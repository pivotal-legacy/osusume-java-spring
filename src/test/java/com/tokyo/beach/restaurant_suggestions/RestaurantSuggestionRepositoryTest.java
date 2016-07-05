package com.tokyo.beach.restaurant_suggestions;

import com.tokyo.beach.restaurants.restaurant_suggestions.RestaurantSuggestion;
import com.tokyo.beach.restaurants.restaurant_suggestions.RestaurantSuggestionRepository;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestaurantSuggestionRepositoryTest {
    @Test
    public void test_getAll_ReturnsRestaurantSuggestions() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody(
                "{\"results\": [{" +
                        "\"place_id\" : \"ChIJ5_kEroKLGGARU4NlyGSJt8Y\"," +
                        "\"name\": \"ＡＦＵＲＩ \"," +
                        "\"formatted_address\": \"〒150-0013 東京都渋谷区恵比寿1-1-7１１７ビル1F\"," +
                        "\"geometry\" : {" +
                            "\"location\" : {" +
                               "\"lat\" : 35.648355, \"lng\" : 139.710893" +
                            "}," +
                            "\"extra-field-we-do-not-use\": {}" +
                        "}" +
                        "}]}"
        ));

        server.start();

        HttpUrl url = server.url("/");


        RestaurantSuggestionRepository repository = new RestaurantSuggestionRepository();
        RestaurantSuggestion restaurantSuggestion = repository.getAll(url).get(0);

        assertThat(restaurantSuggestion.getName(), is("ＡＦＵＲＩ "));
        assertThat(restaurantSuggestion.getAddress(), is("〒150-0013 東京都渋谷区恵比寿1-1-7１１７ビル1F"));
        assertThat(restaurantSuggestion.getPlaceId(), is("ChIJ5_kEroKLGGARU4NlyGSJt8Y"));
        assertThat(restaurantSuggestion.getLatitude(), is(35.648355));
        assertThat(restaurantSuggestion.getLongitude(), is(139.710893));

        server.shutdown();
    }
}
