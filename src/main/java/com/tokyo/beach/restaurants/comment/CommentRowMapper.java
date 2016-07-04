package com.tokyo.beach.restaurants.comment;

import org.springframework.jdbc.core.RowMapper;

public class CommentRowMapper {
    public static RowMapper<Comment> commentRowMapper = (rs, i) ->
            new Comment(
                    rs.getLong("id"),
                    rs.getString("content"),
                    rs.getString("created_at"),
                    rs.getLong("restaurant_id"),
                    rs.getLong("created_by_user_id")
            );
}
