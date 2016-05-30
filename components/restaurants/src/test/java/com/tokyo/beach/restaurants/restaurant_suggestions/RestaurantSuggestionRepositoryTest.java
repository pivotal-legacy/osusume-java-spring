package com.tokyo.beach.restaurants.restaurant_suggestions;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestaurantSuggestionRepositoryTest {
    @Test
    public void test_getAll_ReturnsRestaurantSuggestions() throws IOException {
        MockWebServer server = new MockWebServer();

        server.enqueue(new MockResponse().setBody(
                "{\"rest\": [{" +
                        "\"name\": \"ＡＦＵＲＩ \"," +
                        "\"name_kana\": \"アフリ\"," +
                        "\"latitude\": \"35.645028\"," +
                        "\"longitude\": \"139.714144\"," +
                        "\"address\": \"〒150-0013 東京都渋谷区恵比寿1-1-7１１７ビル1F\"}"
                        + "]}"
        ));

        server.start();

        HttpUrl url = server.url("/");
        List<RestaurantSuggestion> restaurantSuggestions = singletonList(
                new RestaurantSuggestion("ＡＦＵＲＩ ", "〒150-0013 東京都渋谷区恵比寿1-1-7１１７ビル1F")
        );
        RestaurantSuggestionRepository repository = new RestaurantSuggestionRepository();
        assertThat(repository.getAll(url), is(restaurantSuggestions));

        server.shutdown();
    }
}
