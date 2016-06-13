package com.tokyo.beach.restaurant;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.User;
import org.springframework.jdbc.core.JdbcTemplate;

public class RestaurantFixture {
    private long id = 0;
    private String name = "Not Specified";
    private String createdAt = "created-date";
    private String updatedAt = "updated-date";
    private Cuisine cuisine = new Cuisine("Not Specified");
    private PriceRange priceRange = new PriceRange("Not Specified");
    private User user = new User(0, "email@email", "Not Specified");

    public RestaurantFixture withId(long id) {
        this.id = id;
        return this;
    }

    public RestaurantFixture withName(String name) {
        this.name = name;
        return this;
    }

    public RestaurantFixture withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public RestaurantFixture withCuisine(Cuisine cuisine) {
        this.cuisine = cuisine;
        return this;
    }

    public RestaurantFixture withPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
        return this;
    }

    public RestaurantFixture withUser(User user) {
        this.user = user;
        return this;
    }

    public RestaurantFixture withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Restaurant build() {
        return new Restaurant(
                id,
                name,
                "Roppongi",
                false,
                true,
                false,
                "",
                createdAt,
                updatedAt,
                user.getId(),
                priceRange.getId(),
                cuisine.getId()
        );
    }

    public Restaurant persist(JdbcTemplate jdbcTemplate) {
        NewRestaurant newRestaurant = new NewRestaurantFixture().
                withCuisineId(cuisine.getId())
                .withPriceRangeId(priceRange.getId())
                .withRestaurant(this.build())
                .build();

        return TestDatabaseUtils.insertRestaurantIntoDatabase(
                jdbcTemplate,
                newRestaurant,
                user.getId()
        );
    }

}
