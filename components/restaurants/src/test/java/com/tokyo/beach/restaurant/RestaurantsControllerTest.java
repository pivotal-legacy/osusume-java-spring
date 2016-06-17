package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.restaurant.*;
import com.tokyo.beach.restaurants.s3.S3StorageRepository;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restutils.RestControllerExceptionHandler;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.restutils.ControllerTestingUtils.createControllerAdvice;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class RestaurantsControllerTest {
    private RestaurantRepository restaurantRepository;
    private MockMvc mockMvc;
    private PhotoDataMapper photoDataMapper;
    private S3StorageRepository s3StorageRepository;

    @Before
    public void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        photoDataMapper = mock(PhotoDataMapper.class);
        s3StorageRepository = mock(S3StorageRepository.class);

        RestaurantsController restaurantsController = new RestaurantsController(
                restaurantRepository,
                photoDataMapper,
                s3StorageRepository
        );

        mockMvc = standaloneSetup(restaurantsController)
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();
    }

    @Test
    public void test_getAll_returnsAListOfRestaurants() throws Exception {
        List<SerializedRestaurant> restaurants = singletonList(
                new SerializedRestaurant(
                        new Restaurant(
                                1,
                                "Afuri",
                                "Roppongi",
                                false,
                                true,
                                false,
                                "とても美味しい",
                                "2016-04-13 16:01:21.094",
                                "2016-04-14 16:01:21.094",
                                1,
                                1L,
                                20L
                        ),
                        singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1)),
                        Optional.of(new Cuisine(20L, "Swedish")),
                        Optional.of(new PriceRange(1L, "100yen")),
                        Optional.of(new User(1L, "taro@email.com", "taro")),
                        emptyList(),
                        true,
                        2
                )
        );
        when(restaurantRepository.getAll(1L)).thenReturn(restaurants);
        mockMvc.perform(get("/restaurants").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].cuisine.id", equalTo(20)))
                .andExpect(jsonPath("$[0].cuisine.name", equalTo("Swedish")))
                .andExpect(jsonPath("$[0].offers_english_menu", equalTo(false)))
                .andExpect(jsonPath("$[0].walk_ins_ok", equalTo(true)))
                .andExpect(jsonPath("$[0].accepts_credit_cards", equalTo(false)))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].photo_urls[0].id", equalTo(999)))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("http://www.cats.com/my-cat.jpg")))
                .andExpect(jsonPath("$[0].price_range", equalTo("100yen")))
                .andExpect(jsonPath("$[0].num_likes", equalTo(2)))
                .andExpect(jsonPath("$[0].liked", equalTo(true)))
                .andExpect(jsonPath("$[0].created_at", equalTo("2016-04-13T16:01:21.094Z")))
                .andExpect(jsonPath("$[0].updated_at", equalTo("2016-04-14T16:01:21.094Z")))
                .andExpect(jsonPath("$[0].created_by_user_name", equalTo("taro")));
    }

    @Test
    public void test_get_returnsARestaurant() throws Exception {
        SerializedRestaurant restaurant = new SerializedRestaurant(
            new Restaurant(
                    1,
                    "Afuri",
                    "Roppongi",
                    false,
                    true,
                    false,
                    "とても美味しい",
                    "2016-04-13 16:01:21.094",
                    "2016-04-14 16:01:21.094",
                    1,
                    1L,
                    20L
            ),
            singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1)),
            Optional.of(new Cuisine(20L, "Swedish")),
            Optional.of(new PriceRange(1L, "100yen")),
            Optional.of(new User(1L, "taro@email.com", "taro")),
            emptyList(),
            true,
            2
        );
        when(restaurantRepository.get(1L, 1L)).thenReturn(Optional.of(restaurant));
        mockMvc.perform(get("/restaurants/1").requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Afuri")))
                .andExpect(jsonPath("$.address", equalTo("Roppongi")))
                .andExpect(jsonPath("$.cuisine.id", equalTo(20)))
                .andExpect(jsonPath("$.cuisine.name", equalTo("Swedish")))
                .andExpect(jsonPath("$.offers_english_menu", equalTo(false)))
                .andExpect(jsonPath("$.walk_ins_ok", equalTo(true)))
                .andExpect(jsonPath("$.accepts_credit_cards", equalTo(false)))
                .andExpect(jsonPath("$.notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$.photo_urls[0].id", equalTo(999)))
                .andExpect(jsonPath("$.photo_urls[0].url", equalTo("http://www.cats.com/my-cat.jpg")))
                .andExpect(jsonPath("$.price_range", equalTo("100yen")))
                .andExpect(jsonPath("$.num_likes", equalTo(2)))
                .andExpect(jsonPath("$.liked", equalTo(true)))
                .andExpect(jsonPath("$.created_at", equalTo("2016-04-13T16:01:21.094Z")))
                .andExpect(jsonPath("$.updated_at", equalTo("2016-04-14T16:01:21.094Z")))
                .andExpect(jsonPath("$.created_by_user_name", equalTo("taro")));
    }

    @Test
    public void test_getInvalidRestaurantId_throwsException() throws Exception {
        when(restaurantRepository.get(1L, 1L)).thenReturn(
                Optional.empty()
        );


        mockMvc.perform(get("/restaurants/1").requestAttr("userId", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Invalid restaurant id.\"}"));
    }

    @Test
    public void test_create_createsARestaurantAndReturnsIt() throws Exception {
        NewRestaurant newRestaurant = new NewRestaurant(
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "soooo goood",
                2L,
                1L,
                singletonList(new NewPhotoUrl("http://some-url"))
                );
        Long userId = 99L;
        SerializedRestaurant restaurant = new SerializedRestaurant(
                new Restaurant(
                        1,
                        newRestaurant.getName(),
                        newRestaurant.getAddress(),
                        newRestaurant.getOffersEnglishMenu(),
                        newRestaurant.getWalkInsOk(),
                        newRestaurant.getAcceptsCreditCards(),
                        newRestaurant.getNotes(),
                        "2016-04-13 16:01:21.094",
                        "2016-04-14 16:01:21.094",
                        userId,
                        newRestaurant.getPriceRangeId(),
                        newRestaurant.getCuisineId()
                ),
                singletonList(new PhotoUrl(1, "http://some-url", 1)),
                Optional.of(new Cuisine(2, "Ramen")),
                Optional.of(new PriceRange(1, "~900")),
                Optional.of(new User(99, "email", "jiro")),
                emptyList(),
                false,
                0
        );
        when(restaurantRepository.create(newRestaurant, userId)).
                thenReturn(restaurant);
        String payload =
            "{" +
            "\"restaurant\": " +
            "{" +
            "\"name\":\"Afuri\", " +
            "\"address\": \"Roppongi\", " +
            "\"offers_english_menu\": false, " +
            "\"walk_ins_ok\": true, " +
            "\"accepts_credit_cards\": false, " +
            "\"notes\": \"soooo goood\", " +
            "\"photo_urls\": [{\"url\": \"http://some-url\"}], " +
            "\"cuisine_id\": \"2\", " +
            "\"price_range_id\": \"1\"" +
            "}" +
            "}";


        mockMvc.perform(
            post("/restaurants")
            .requestAttr("userId", userId)
            .contentType(APPLICATION_JSON_UTF8_VALUE)
            .content(payload)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("Afuri")))
        .andExpect(jsonPath("$.address", is("Roppongi")))
        .andExpect(jsonPath("$.offers_english_menu", is(false)))
        .andExpect(jsonPath("$.walk_ins_ok", is(true)))
        .andExpect(jsonPath("$.accepts_credit_cards", is(false)))
        .andExpect(jsonPath("$.notes", is("soooo goood")))
        .andExpect(jsonPath("$.photo_urls[0].url", is("http://some-url")))
        .andExpect(jsonPath("$.cuisine.name", is("Ramen")))
        .andExpect(jsonPath("$.price_range", is("~900")))
        .andExpect(jsonPath("$.created_at", Matchers.equalTo("2016-04-13T16:01:21.094Z")))
        .andExpect(jsonPath("$.updated_at", Matchers.equalTo("2016-04-14T16:01:21.094Z")))
        .andExpect(jsonPath("$.created_by_user_name", is("jiro")));
    }

    @Test
    public void test_update_updatesRestaurantInformation() throws Exception {
        NewRestaurant newRestaurant = new NewRestaurant(
                "Updated Name",
                "Updated Address",
                false,
                true,
                false,
                "",
                2L,
                null,
                singletonList(new NewPhotoUrl("http://some-url"))
        );
        Long userId = 99L;
        SerializedRestaurant restaurant = new SerializedRestaurant(
                new Restaurant(
                        1,
                        newRestaurant.getName(),
                        newRestaurant.getAddress(),
                        newRestaurant.getOffersEnglishMenu(),
                        newRestaurant.getWalkInsOk(),
                        newRestaurant.getAcceptsCreditCards(),
                        newRestaurant.getNotes(),
                        "2016-04-13 16:01:21.094",
                        "2016-04-14 16:01:21.094",
                        userId,
                        0,
                        newRestaurant.getCuisineId()
                ),
                singletonList(new PhotoUrl(1, "http://some-url", 1)),
                Optional.of(new Cuisine(2, "Ramen")),
                Optional.of(new PriceRange(1, "~900")),
                Optional.of(new User(99, "email", "jiro")),
                emptyList(),
                false,
                0
        );

        when(restaurantRepository.update(1L, newRestaurant)).
                thenReturn(restaurant);
        String updatedRestaurantPayload = "{\"restaurant\": " +
                "{\"name\":\"Updated Name\", " +
                "\"address\": \"Updated Address\", " +
                "\"offers_english_menu\": false, " +
                "\"walk_ins_ok\": true, " +
                "\"accepts_credit_cards\": false, " +
                "\"notes\": \"\"," +
                "\"photo_urls\": [{\"url\": \"http://some-url\"}], " +
                "\"cuisine_id\": \"2\"}" +
                "}";

        mockMvc.perform(
                patch("/restaurants/1")
                        .requestAttr("userId", 99L)
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .content(updatedRestaurantPayload)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.address", is("Updated Address")))
                .andExpect(jsonPath("$.offers_english_menu", is(false)))
                .andExpect(jsonPath("$.walk_ins_ok", is(true)))
                .andExpect(jsonPath("$.accepts_credit_cards", is(false)))
                .andExpect(jsonPath("$.notes", is("")))
                .andExpect(jsonPath("$.photo_urls[0].url", is("http://some-url")))
                .andExpect(jsonPath("$.cuisine.name", is("Ramen")))
                .andExpect(jsonPath("$.created_by_user_name", is("jiro")))
                .andExpect(jsonPath("$.price_range", is("~900")));
    }



    @Test
    public void test_delete_returnsOkHTTPStatus() throws Exception {
        when(photoDataMapper.get(
                anyLong()
        )).thenReturn(Optional.empty());


        ResultActions result = mockMvc.perform(delete("/restaurants/10/photoUrls/20")
                .requestAttr("userId", 11L)
        );


        result.andExpect(status().isOk());
    }

    @Test
    public void test_delete_deletesPhotoUrlMadeByCurrentUser() throws Exception {
        when(photoDataMapper.get(10))
                .thenReturn(Optional.of(
                        new PhotoUrl(
                                10,
                                "http://hoge/image.jpg",
                                20
                        )
                ));

        ResultActions result = mockMvc.perform(delete("/restaurants/20/photoUrls/10")
                .requestAttr("userId", 99));

        result.andExpect(status().isOk());
        verify(photoDataMapper, times(1)).get(10);
        verify(photoDataMapper, times(1)).delete(10);
        verify(s3StorageRepository, times(1)).deleteFile("http://hoge/image.jpg");
    }


    @Test
    public void test_delete_doesntDeleteNonExistentPhotoUrl() throws Exception {
        when(photoDataMapper.get(10))
                .thenReturn(Optional.empty()
                );

        ResultActions result = mockMvc.perform(delete("/restaurants/20/photoUrls/10")
                .requestAttr("userId", 99));

        result.andExpect(status().isOk());
        verify(photoDataMapper, times(1)).get(10);
        verify(photoDataMapper, never()).delete(10);
        verify(s3StorageRepository, never()).deleteFile(anyString());
    }
}
