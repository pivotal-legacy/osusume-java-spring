package com.tokyo.beach.photos;

import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.user.UserFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.*;
import static com.tokyo.beach.restaurants.photos.PhotoUrlRowMapper.photoUrlRowMapper;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PhotoDataMapperTest {

    private PhotoDataMapper photoDataMapper;
    private JdbcTemplate jdbcTemplate;
    private Restaurant restaurant;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        photoDataMapper = new PhotoDataMapper(jdbcTemplate);
        createDefaultCuisine(jdbcTemplate);
        createDefaultPriceRange(jdbcTemplate);
        User user = new UserFixture().persist(jdbcTemplate);
        restaurant = new RestaurantFixture()
                .withUser(user)
                .persist(jdbcTemplate);
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_findForRestaurants_returnsPhotoUrlList() throws Exception {
        PhotoUrl photoUrl  = new PhotoUrlFixture()
                .withUrl("http://some-url")
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);

        List<PhotoUrl> photos = photoDataMapper.findForRestaurants(singletonList(restaurant.getId()));

        assertThat(photos, hasSize(1));

        PhotoUrl firstPhoto = photos.get(0);

        assertThat(firstPhoto.getRestaurantId(), is(photoUrl.getRestaurantId()));
    }

    @Test
    public void test_createPhotosForRestaurant() throws Exception {

        photoDataMapper.createPhotosForRestaurant(
                restaurant.getId(),
                asList(
                        new NewPhotoUrl("http://some-url"),
                        new NewPhotoUrl("http://another-url")
                )
        );

        List<PhotoUrl> photoUrls = jdbcTemplate.query(
                "SELECT * FROM photo_url where restaurant_id = ?",
                photoUrlRowMapper,
                restaurant.getId()
        );

        assertThat(photoUrls, notNullValue());
        assertThat(photoUrls.get(0).getUrl(), is("http://some-url"));
        assertThat(photoUrls.get(0).getRestaurantId(), is(restaurant.getId()));
        assertThat(photoUrls.get(1).getUrl(), is("http://another-url"));
        assertThat(photoUrls.get(1).getRestaurantId(), is(restaurant.getId()));
    }

    @Test
    public void test_findForRestaurant_returnsPhotoUrlList() throws Exception {
        PhotoUrl photoUrl = new PhotoUrlFixture()
                .withUrl("http://some-url")
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);

        List<PhotoUrl> photos = photoDataMapper.findForRestaurant(restaurant.getId());

        assertThat(photos, hasSize(1));
        assertThat(photos.get(0).getRestaurantId(), is(restaurant.getId()));
        assertThat(photos.get(0).getUrl(), is(photoUrl.getUrl()));
    }

    @Test
    public void test_get_returnsPhotoUrl() throws Exception {
        PhotoUrl photoUrl = new PhotoUrlFixture()
                .withUrl("http://url.com")
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);

        Optional<PhotoUrl> actualPhotoUrl = photoDataMapper.get(photoUrl.getId());

        assertThat(actualPhotoUrl.get(), is(photoUrl));
    }

    @Test
    public void test_get_returnsEmptyWhenNonexistentPhotoUrl() throws Exception {
        Optional<PhotoUrl> actualPhotoUrl = photoDataMapper.get(99);

        assertFalse(actualPhotoUrl.isPresent());
    }

    @Test
    public void test_delete_deletesPhotoUrl() throws  Exception {
        PhotoUrl photoUrl = new PhotoUrlFixture()
                .withUrl("http://url.com")
                .withRestaurantId(restaurant.getId())
                .persist(jdbcTemplate);

        photoDataMapper.delete(photoUrl.getId());

        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM photo_url WHERE id = ?",
                new Object[]{photoUrl.getId()},
                Integer.class
        );

        assertEquals(0, count);
    }
}
