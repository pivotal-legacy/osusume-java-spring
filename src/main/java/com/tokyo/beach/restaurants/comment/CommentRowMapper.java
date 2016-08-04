package com.tokyo.beach.restaurants.comment;

import org.springframework.jdbc.core.RowMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CommentRowMapper {
    public static RowMapper<Comment> commentRowMapper = (rs, i) ->
            new Comment(
                    rs.getLong("id"),
                    rs.getString("content"),
                    ZonedDateTime.ofInstant(rs.getTimestamp("created_at").toInstant(), ZoneId.of("UTC")),
                    rs.getLong("restaurant_id"),
                    rs.getLong("created_by_user_id")
            );
}
