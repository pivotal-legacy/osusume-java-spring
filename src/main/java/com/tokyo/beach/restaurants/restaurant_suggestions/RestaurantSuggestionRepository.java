package com.tokyo.beach.restaurants.restaurant_suggestions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
            JsonNode node = objectMapper.readValue(responseString, JsonNode.class);

            List<RestaurantSuggestion> suggestions = new ArrayList<>();

            ArrayNode restNodeArray = (ArrayNode) node.get("results");
            for (Iterator<JsonNode> it = restNodeArray.elements(); it.hasNext();) {
                suggestions.add(objectMapper.readValue(it.next().toString(), RestaurantSuggestion.class));
            }

            return suggestions;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
