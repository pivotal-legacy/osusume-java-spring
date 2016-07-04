package com.tokyo.beach.restaurants.photos;

import org.springframework.jdbc.core.RowMapper;

public class PhotoUrlRowMapper {
    public static RowMapper<PhotoUrl> photoUrlRowMapper = (rs, i) ->
            new PhotoUrl(
                    rs.getLong("id"),
                    rs.getString("url"),
                    rs.getLong("restaurant_id")
            );
}
