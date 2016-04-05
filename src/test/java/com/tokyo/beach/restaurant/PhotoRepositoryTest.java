package com.tokyo.beach.restaurant;

import com.tokyo.beach.TestUtils;
import com.tokyo.beach.application.photos.NewPhotoUrl;
import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.application.restaurant.PhotoRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class PhotoRepositoryTest {

    private PhotoRepository photoRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(TestUtils.buildDataSource());
        photoRepository = new PhotoRepository(jdbcTemplate);
    }

    @After
    public void tearDown() throws Exception {
        jdbcTemplate.update("DELETE FROM photo_url");
    }

    @Test
    public void test_findForRestaurants_returnsPhotoUrlList() throws Exception {
        jdbcTemplate.update("INSERT INTO photo_url (url, restaurant_id) VALUES ('http://some-url', 1)");

        List<PhotoUrl> photos = photoRepository.findForRestaurants(singletonList(
                RestaurantFixtures.newRestaurant(1)
        ));

        assertThat(photos, hasSize(1));

        PhotoUrl firstPhoto = photos.get(0);

        assertThat(firstPhoto.getRestaurantId(), is(1));
    }

    @Test
    public void test_createPhotosForRestaurant() throws Exception {
        photoRepository.createPhotosForRestaurant(
                789,
                asList(
                        new NewPhotoUrl("http://some-url"),
                        new NewPhotoUrl("http://another-url")
                )
        );

        List<PhotoUrl> photoUrls = jdbcTemplate.query(
                "SELECT * FROM photo_url where restaurant_id = 789",
                (rs, rowNum) -> {
                    return new PhotoUrl(
                            rs.getInt("id"),
                            rs.getString("url"),
                            rs.getInt("restaurant_id")
                    );
                }
        );

        assertThat(photoUrls, notNullValue());
        assertThat(photoUrls.get(0).getUrl(), is("http://some-url"));
        assertThat(photoUrls.get(0).getRestaurantId(), is(789));
        assertThat(photoUrls.get(1).getUrl(), is("http://another-url"));
        assertThat(photoUrls.get(1).getRestaurantId(), is(789));
    }

    @Test
    public void test_findForRestaurant_returnsPhotoUrlList() throws Exception {
        jdbcTemplate.update("INSERT INTO photo_url (url, restaurant_id) VALUES ('http://some-url', 1)");


        List<PhotoUrl> photos = photoRepository.findForRestaurant(RestaurantFixtures.newRestaurant(1));


        assertThat(photos, hasSize(1));
        assertThat(photos.get(0).getRestaurantId(), is(1));
    }
}
