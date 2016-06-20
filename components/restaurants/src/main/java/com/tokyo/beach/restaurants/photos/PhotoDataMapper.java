package com.tokyo.beach.restaurants.photos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PhotoDataMapper {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PhotoDataMapper(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PhotoUrl> findForRestaurants(List<Long> restaurantIds) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", restaurantIds);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM photo_url WHERE restaurant_id IN (:ids)",
                parameters,
                PhotoDataMapper::mapRow
        );
    }

    public List<PhotoUrl> createPhotosForRestaurant(long restaurantId, List<NewPhotoUrl> photos) {
        List<PhotoUrl> savedPhotos = new ArrayList<>();

        for (NewPhotoUrl photo : photos) {
            PhotoUrl photoUrl = jdbcTemplate.queryForObject(
                    "INSERT INTO photo_url (url, restaurant_id) VALUES (?, ?) RETURNING *",
                    PhotoDataMapper::mapRow,
                    photo.getUrl(),
                    restaurantId
            );

            savedPhotos.add(photoUrl);
        }

        return savedPhotos;
    }

    public List<PhotoUrl> findForRestaurant(long restaurantId) {
        return jdbcTemplate.query(
                "SELECT * FROM photo_url WHERE restaurant_id = ?",
                new Object[]{ restaurantId },
                PhotoDataMapper::mapRow
        );
    }

    public Optional<PhotoUrl> get(long photoUrlId) {
        List<PhotoUrl> photoUrls = jdbcTemplate
                .query("SELECT * FROM photo_url WHERE id = ?",
                        PhotoDataMapper::mapRow,
                        photoUrlId
                );
        if  (photoUrls.size() > 0) {
            return Optional.of(photoUrls.get(0));
        }
        return Optional.empty();
    }

    public void delete(long photoUrlId) {
        jdbcTemplate.update("DELETE FROM photo_url WHERE id = ?", photoUrlId);
    }

    private static PhotoUrl mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PhotoUrl(
                rs.getLong("id"),
                rs.getString("url"),
                rs.getLong("restaurant_id")
        );
    }
}
