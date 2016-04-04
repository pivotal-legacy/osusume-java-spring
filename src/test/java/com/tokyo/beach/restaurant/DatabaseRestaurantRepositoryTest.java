package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.application.restaurant.DatabaseRestaurantRepository;
import com.tokyo.beach.application.restaurant.NewRestaurant;
import com.tokyo.beach.application.restaurant.Restaurant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static com.tokyo.beach.ControllerTestingUtils.buildDataSource;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatabaseRestaurantRepositoryTest {
    private DatabaseRestaurantRepository restaurantRepository;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(buildDataSource());
        restaurantRepository = new DatabaseRestaurantRepository(jdbcTemplate);
    }

    @After
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM restaurant");
        jdbcTemplate.update("DELETE FROM photo_url");
    }

    @Test
    public void test_getAll() {
        Integer restaurantId = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant " +
                        "(name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes)" +
                        "VALUES ('Afuri', 'Roppongi', FALSE, TRUE, FALSE, '')" +
                        "RETURNING *",
                ((rs, rowNum) -> {
                    return rs.getInt("id");
                })
        );

        List<Restaurant> restaurants = restaurantRepository.getAll();


        Restaurant expectedRestaurant = new Restaurant(
                restaurantId,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                emptyList()
        );

        assertThat(restaurants, is(singletonList(expectedRestaurant)));
    }

    @Test
    public void testCreateRestaurantWithoutPhotoUrls() throws Exception {
        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                emptyList()
        );


        restaurantRepository.createRestaurant(kfcNewRestaurant);


        List<Restaurant> actualRestaurantList = jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE name = 'KFC'",
                (rs, rowNum) -> new Restaurant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getBoolean("offers_english_menu"),
                        rs.getBoolean("walk_ins_ok"),
                        rs.getBoolean("accepts_credit_cards"),
                        rs.getString("notes"),
                        emptyList()
                )
        );

        for (Restaurant restaurant : actualRestaurantList) {
            List<PhotoUrl> photoUrls = jdbcTemplate.query(
                    "SELECT * FROM photo_url WHERE restaurant_id = ?",
                    new Object[]{restaurant.getId()},
                    (rs, rowNum) -> {
                        return new PhotoUrl(
                                rs.getInt("id"),
                                rs.getString("url"),
                                restaurant.getId()
                        );
                    }
            );
            restaurant.setPhotoUrlList(photoUrls);
        }
        assertThat(actualRestaurantList.get(0).getName(), is("KFC"));
        assertThat(actualRestaurantList.get(0).getPhotoUrlList().size(), is(0));
    }

    @Test
    public void testCreateRestaurantWithPhotoUrls() throws Exception {
        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                emptyList()
        );


        restaurantRepository.createRestaurant(kfcNewRestaurant);


        List<Restaurant> actualRestaurantList = jdbcTemplate.query(
                "SELECT * FROM restaurant WHERE name = 'KFC'",
                (rs, rowNum) -> new Restaurant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getBoolean("offers_english_menu"),
                        rs.getBoolean("walk_ins_ok"),
                        rs.getBoolean("accepts_credit_cards"),
                        rs.getString("notes"),
                        emptyList()
                )
        );

        for (Restaurant restaurant : actualRestaurantList) {
            List<PhotoUrl> actualPhotoUrls = jdbcTemplate.query(
                    "SELECT * FROM photo_url WHERE restaurant_id = ?",
                    new Object[]{restaurant.getId()},
                    (rs, rowNum) -> {
                        return new PhotoUrl(
                                rs.getInt("id"),
                                rs.getString("url"),
                                restaurant.getId()
                        );
                    }
            );
            restaurant.setPhotoUrlList(actualPhotoUrls);
        }
        assertThat(actualRestaurantList.get(0).getName(), is("KFC"));
    }
}
