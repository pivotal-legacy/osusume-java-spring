package com.tokyo.beach.restaurants.restaurant_suggestions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantSuggestion {
    private String placeId;
    private String name;
    private String address;

    public RestaurantSuggestion() {

    }

    public RestaurantSuggestion(String placeId, String name, String address) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("formatted_address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("place_id")
    public String getPlaceId() {
        return placeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantSuggestion that = (RestaurantSuggestion) o;

        if (placeId != null ? !placeId.equals(that.placeId) : that.placeId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        int result = placeId != null ? placeId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }
}
