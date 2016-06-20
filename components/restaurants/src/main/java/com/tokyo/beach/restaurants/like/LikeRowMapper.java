package com.tokyo.beach.restaurants.like;

import org.springframework.jdbc.core.RowMapper;

public class LikeRowMapper {
    public static RowMapper<Like> likeRowMapper = (rs, i) ->
            new Like(
                    rs.getLong("user_id"),
                    rs.getLong("restaurant_id")
            );
}
