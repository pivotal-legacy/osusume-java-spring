package com.tokyo.beach.photos;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import org.springframework.jdbc.core.JdbcTemplate;

public class PhotoUrlFixture {
    private String url;
    private long restaurantId;

    public PhotoUrlFixture withUrl(String url) {
        this.url = url;
        return this;
    }

    public PhotoUrlFixture withRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
        return this;
    }

    public PhotoUrl persist(JdbcTemplate jdbcTemplate) {
        return TestDatabaseUtils.insertPhotoUrlIntoDatabase(jdbcTemplate, new PhotoUrl(0L, url,restaurantId));
    }
}
