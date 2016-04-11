package com.tokyo.beach.application.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DatabaseCommentRepository implements CommentRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Comment create(NewComment newComment, long createdByUserId, String restaurantId) {
        String sql = "INSERT INTO comment (content, restaurant_id, created_by_user_id) VALUES (?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {
                    return new Comment(
                            rs.getLong("id"),
                            rs.getString("content"),
                            rs.getString("created_at"),
                            rs.getLong("restaurant_id"),
                            rs.getLong("created_by_user_id")
                    );
                },
                newComment.getContent(),
                Long.parseLong(restaurantId),
                createdByUserId
        );
    }
}
