package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.TestUtils;
import com.tokyo.beach.application.restaurant.PhotoRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PhotoRepositoryTest {

    private PhotoRepository photoRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = new JdbcTemplate(TestUtils.buildDataSource());
        photoRepository = new PhotoRepository(jdbcTemplate);
    }

    @Test
    public void test_findForRestaurants_returnsPhotoUrlList() throws Exception {
        try {
            jdbcTemplate.update("INSERT INTO photo_url (url, restaurant_id) VALUES ('http://some-url', 1)");

            List<PhotoUrl> photos = photoRepository.findForRestaurants(singletonList(
                    RestaurantFixtures.newRestaurant(1)
            ));

            assertThat(photos, hasSize(1));

            PhotoUrl firstPhoto = photos.get(0);

            assertThat(firstPhoto.getRestaurantId(), is(1));
        } finally {
            jdbcTemplate.update("DELETE FROM photo_url");
        }
    }
}
