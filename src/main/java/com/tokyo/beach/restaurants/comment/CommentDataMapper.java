package com.tokyo.beach.restaurants.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.restaurants.comment.CommentRowMapper.commentRowMapper;

@Repository
public class CommentDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentDataMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Comment create(NewComment newComment, long createdByUserId, long restaurantId) {
        String sql = "INSERT INTO comment (content, restaurant_id, created_by_user_id) VALUES (?, ?, ?) RETURNING *";
        return jdbcTemplate.queryForObject(
                sql,
                commentRowMapper,
                newComment.getComment(),
                restaurantId,
                createdByUserId
        );
    }


    public Optional<Comment> get(long commentId) {
        List<Comment> comments = jdbcTemplate.query(
                "SELECT * FROM comment WHERE id = ?",
                commentRowMapper,
                commentId
        );

        if ( comments.size() > 0 ) {
            return Optional.of(comments.get(0));
        } else {
            return Optional.empty();
        }
    }

    public void delete(long commentId) {
        jdbcTemplate.update("DELETE FROM comment WHERE id = ?", commentId);
    }
}
