package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.restaurant.DatabaseRestaurantRepository;
import com.tokyo.beach.application.restaurant.NewRestaurant;
import com.tokyo.beach.application.restaurant.Restaurant;
import com.tokyo.beach.application.photos.NewPhotoUrl;
import com.tokyo.beach.application.photos.PhotoUrl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class DatabaseRestaurantRepositoryTest {
    DatabaseRestaurantRepository restaurantRepository;
    JdbcTemplate jdbcTemplate;

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
    public void testGetAllWithPhotoUrls() {
        Integer restaurantId = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant " +
                        "(name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes)" +
                        "VALUES ('Afuri', 'Roppongi', false, true, false, '')" +
                        "RETURNING *",
                ((rs, rowNum) -> {
                    return rs.getInt("id");
                })
        );
        Integer photoUrlId = jdbcTemplate.queryForObject(
                "INSERT INTO photo_url (url, restaurant_id) " +
                        "VALUES ('url'," + restaurantId + ") " +
                        "RETURNING restaurant_id",
                ((rs, rowNum) -> {
                    return rs.getInt("restaurant_id");
                })
        );

        List<Restaurant> restaurants = restaurantRepository.getAll();


        PhotoUrl expectedPhotoUrl = new PhotoUrl(photoUrlId, "url", restaurantId);
        List<PhotoUrl> photoUrls = new ArrayList<PhotoUrl>(){{ add(expectedPhotoUrl); }};
        Restaurant expectedRestaurant = new Restaurant(
                restaurantId,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                photoUrls
        );
        assertThat(restaurants, is(Collections.singletonList(expectedRestaurant)));
    }

    @Test
    public void testGetAllWithoutPhotoUrls() {
        Integer restaurantId = jdbcTemplate.queryForObject(
                "INSERT INTO restaurant " +
                        "(name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes)" +
                        "VALUES ('Afuri', 'Roppongi', false, true, false, '')" +
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
                new ArrayList()
        );
        assertThat(restaurants, is(Collections.singletonList(expectedRestaurant)));
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void testCreateRestaurantWithoutPhotoUrls() throws Exception {
        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                new ArrayList<>()
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
                        new ArrayList()
                )
        );

        for(Restaurant restaurant: actualRestaurantList) {
            List<PhotoUrl> photoUrls = jdbcTemplate.query(
                    "SELECT * FROM photo_url WHERE restaurant_id = ?",
                    new Object[]{ restaurant.getId() },
                    (rs, rowNum) ->{
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

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void testCreateRestaurantWithPhotoUrls() throws Exception {
        ArrayList<NewPhotoUrl> photoUrls = new ArrayList<>();
        photoUrls.add( new NewPhotoUrl("url") );
        NewRestaurant kfcNewRestaurant = new NewRestaurant(
                "KFC",
                "Shibuya",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                "Notes",
                photoUrls
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
                        new ArrayList()
                )
        );

        for(Restaurant restaurant: actualRestaurantList) {
            List<PhotoUrl> actualPhotoUrls = jdbcTemplate.query(
                    "SELECT * FROM photo_url WHERE restaurant_id = ?",
                    new Object[]{ restaurant.getId() },
                    (rs, rowNum) ->{
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
        assertThat(actualRestaurantList.get(0).getPhotoUrlList().size(), is(1));
    }

    private DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
