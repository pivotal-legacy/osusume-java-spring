package com.tokyo.beach.restaurants.restaurant_suggestions;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class RestaurantSuggestionRepository {
    private OkHttpClient okHttpClient;

    public RestaurantSuggestionRepository() {
        this.okHttpClient = new OkHttpClient();
    }

    public List<RestaurantSuggestion> getAll(HttpUrl url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseString = response.body().string();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(responseString, GooglePlacesResult.class).getResults();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
