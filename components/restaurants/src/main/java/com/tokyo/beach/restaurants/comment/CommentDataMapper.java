package com.tokyo.beach.restaurants.comment;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentDataMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
                newComment.getComment(),
                Long.parseLong(restaurantId),
                createdByUserId
        );
    }

    public List<SerializedComment> findForRestaurant(long restaurantId) {
        return jdbcTemplate.query(
                "SELECT comment.id as comment_id, users.id as user_id, * FROM comment " +
                        "inner join users on comment.created_by_user_id = users.id " +
                        "where restaurant_id = ?",
                (rs, rowNum) -> {

                    return new SerializedComment(
                            new Comment(
                                    rs.getLong("comment_id"),
                                    rs.getString("content"),
                                    rs.getString("created_at"),
                                    rs.getLong("restaurant_id"),
                                    rs.getLong("created_by_user_id")
                            ),
                            new User(
                                    rs.getLong("user_id"),
                                    rs.getString("email"),
                                    rs.getString("name")
                            )

                    );
                },
                restaurantId
        );
    }

    public Optional<Comment> get(long commentId) {
        List<Comment> comments = jdbcTemplate.query(
                "SELECT * FROM comment WHERE id = ?",
                (rs, rowNum) -> {
                    return new Comment(
                            rs.getLong("id"),
                            rs.getString("content"),
                            rs.getString("created_at"),
                            rs.getLong("restaurant_id"),
                            rs.getLong("created_by_user_id")
                    );
                },
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
