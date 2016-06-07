package com.tokyo.beach.restaurants.photos;

import com.tokyo.beach.restaurants.restaurant.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
public class DatabasePhotoRepository implements PhotoRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabasePhotoRepository(@SuppressWarnings("SpringJavaAutowiringInspection") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PhotoUrl> findForRestaurants(List<Restaurant> restaurants) {
        List<Long> ids = restaurants.stream().map(Restaurant::getId).collect(toList());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        return namedTemplate.query(
                "SELECT * FROM photo_url WHERE restaurant_id IN (:ids)",
                parameters,
                (rs, rowNum) -> {
                    return new PhotoUrl(rs.getLong("id"), rs.getString("url"), rs.getLong("restaurant_id"));
                }

        );
    }

    @Override
    public List<PhotoUrl> createPhotosForRestaurant(long restaurantId, List<NewPhotoUrl> photos) {
        List<PhotoUrl> savedPhotos = new ArrayList<>();

        for (NewPhotoUrl photo : photos) {
            PhotoUrl photoUrl = jdbcTemplate.queryForObject(
                    "INSERT INTO photo_url (url, restaurant_id) VALUES (?, ?) RETURNING *",
                    (rs, rowNum) -> {
                        return new PhotoUrl(rs.getLong("id"), rs.getString("url"), rs.getLong("restaurant_id"));
                    },
                    photo.getUrl(),
                    restaurantId
            );

            savedPhotos.add(photoUrl);
        }

        return savedPhotos;
    }

    @Override
    public List<PhotoUrl> findForRestaurant(Restaurant restaurant) {
        return jdbcTemplate.query(
                "SELECT * FROM photo_url WHERE restaurant_id = ?",
                new Object[]{ restaurant.getId() },
                (rs, rowNum) -> {
                    return new PhotoUrl(
                            rs.getLong("id"),
                            rs.getString("url"),
                            rs.getLong("restaurant_id")
                    );
                }
        );
    }

    @Override
    public Optional<PhotoUrl> get(long photoUrlId) {
        List<PhotoUrl> photoUrls = jdbcTemplate
                .query("SELECT * FROM photo_url WHERE id = ?",
                        (rs, rowNum) -> {
                            return new PhotoUrl(
                                rs.getLong("id"),
                                rs.getString("url"),
                                rs.getLong("restaurant_id")
                            );
                        },
                        photoUrlId
                );
        if  (photoUrls.size() > 0) {
            return Optional.of(photoUrls.get(0));
        }
        return Optional.empty();
    }

    @Override
    public void delete(long photoUrlId) {

    }
}
