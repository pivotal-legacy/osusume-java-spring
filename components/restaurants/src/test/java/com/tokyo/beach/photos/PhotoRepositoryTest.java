package com.tokyo.beach.photos;

import com.tokyo.beach.TestDatabaseUtils;
import com.tokyo.beach.restaurant.RestaurantFixture;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.photos.PhotoRepository;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.truncateAllTables;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class PhotoRepositoryTest {

    private PhotoRepository photoRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(TestDatabaseUtils.buildDataSource());
        photoRepository = new PhotoRepository(jdbcTemplate);
    }

    @After
    public void tearDown() throws Exception {
        truncateAllTables(jdbcTemplate);
    }

    @Test
    public void test_findForRestaurants_returnsPhotoUrlList() throws Exception {
        TestDatabaseUtils.insertPhotoUrlIntoDatabase(
                jdbcTemplate,
                new PhotoUrl(0, "http://some-url", 1)
        );

        List<PhotoUrl> photos = photoRepository.findForRestaurants(singletonList(
                new RestaurantFixture().withId(1L).build()
        ));

        assertThat(photos, hasSize(1));

        PhotoUrl firstPhoto = photos.get(0);

        assertThat(firstPhoto.getRestaurantId(), is(1L));
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
                            rs.getLong("id"),
                            rs.getString("url"),
                            rs.getLong("restaurant_id")
                    );
                }
        );

        assertThat(photoUrls, notNullValue());
        assertThat(photoUrls.get(0).getUrl(), is("http://some-url"));
        assertThat(photoUrls.get(0).getRestaurantId(), is(789L));
        assertThat(photoUrls.get(1).getUrl(), is("http://another-url"));
        assertThat(photoUrls.get(1).getRestaurantId(), is(789L));
    }

    @Test
    public void test_findForRestaurant_returnsPhotoUrlList() throws Exception {
        PhotoUrl photoUrl = TestDatabaseUtils.insertPhotoUrlIntoDatabase(
                jdbcTemplate,
                new PhotoUrl(0, "http://some-url", 1)
        );


        List<PhotoUrl> photos = photoRepository.findForRestaurant(new RestaurantFixture().withId(1L).build());


        assertThat(photos, hasSize(1));
        assertThat(photos.get(0).getRestaurantId(), is(1L));
    }

    @Test
    public void test_getPhotoUrl_returnsPhotoUrl() throws Exception {
        PhotoUrl photoUrl = TestDatabaseUtils.insertPhotoUrlIntoDatabase(
                jdbcTemplate,
                new PhotoUrl(0, "http://url.com", 10)
        );


        Optional<PhotoUrl> actualPhotoUrl = photoRepository.get(photoUrl.getId());


        assertThat(actualPhotoUrl.get(), is(photoUrl));
    }

    @Test
    public void test_getNonexistentPhotoUrl_returnsEmpty() throws Exception {
        Optional<PhotoUrl> actualPhotoUrl = photoRepository.get(99);


        assertFalse(actualPhotoUrl.isPresent());
    }

    @Test
    public void test_delete_deletesPhotoUrl() throws  Exception {
        PhotoUrl photoUrl = TestDatabaseUtils.insertPhotoUrlIntoDatabase(
                jdbcTemplate,
                new PhotoUrl(0, "http://url.com", 10)
        );


        photoRepository.delete(photoUrl.getId());


        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM photo_url WHERE id = ?",
                new Object[]{photoUrl.getId()},
                Integer.class
        );

        assertEquals(0, count);
    }
}
