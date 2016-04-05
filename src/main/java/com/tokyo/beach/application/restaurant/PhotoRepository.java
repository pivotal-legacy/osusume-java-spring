package com.tokyo.beach.application.restaurant;

import com.tokyo.beach.application.photos.NewPhotoUrl;
import com.tokyo.beach.application.photos.PhotoUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class PhotoRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PhotoRepository(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PhotoUrl> findForRestaurants(List<Restaurant> restaurants) {
        List<Integer> ids = restaurants.stream().map(Restaurant::getId).collect(toList());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM photo_url WHERE restaurant_id IN (:ids)",
                parameters,
                (rs, rowNum) -> {
                    return new PhotoUrl(rs.getInt("id"), rs.getString("url"), rs.getInt("restaurant_id"));
                }

        );
    }

    public List<PhotoUrl> createPhotosForRestaurant(long restaurantId, List<NewPhotoUrl> photos) {
        List<PhotoUrl> savedPhotos = new ArrayList<>();

        for (NewPhotoUrl photo : photos) {
            PhotoUrl photoUrl = jdbcTemplate.queryForObject(
                    "INSERT INTO photo_url (url, restaurant_id) VALUES (?, ?) RETURNING *",
                    (rs, rowNum) -> {
                        return new PhotoUrl(rs.getInt("id"), rs.getString("url"), rs.getInt("restaurant_id"));
                    },
                    photo.getUrl(),
                    restaurantId
            );

            savedPhotos.add(photoUrl);
        }

        return savedPhotos;
    }

    public List<PhotoUrl> findForRestaurant(Restaurant restaurant) {
        return jdbcTemplate.query(
                "SELECT * FROM photo_url WHERE restaurant_id = ?",
                new Object[]{ restaurant.getId() },
                (rs, rowNum) -> {
                    return new PhotoUrl(
                            rs.getInt("id"),
                            rs.getString("url"),
                            rs.getInt("restaurant_id")
                    );
                }
        );
    }
}
