package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.comment.CommentDataMapper;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.restaurant.NewRestaurant;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantDataMapper;
import com.tokyo.beach.restaurants.restaurant.RestaurantsController;
import com.tokyo.beach.restaurants.s3.StorageRepository;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import com.tokyo.beach.restutils.RestControllerExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static com.tokyo.beach.restutils.ControllerTestingUtils.createControllerAdvice;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class RestaurantsControllerTest {
    private RestaurantDataMapper restaurantDataMapper;
    private MockMvc mockMvc;
    private PhotoDataMapper photoDataMapper;
    private CuisineDataMapper cuisineDataMapper;
    private UserDataMapper userDataMapper;
    private CommentDataMapper commentDataMapper;
    private LikeDataMapper likeDataMapper;
    private PriceRangeDataMapper priceRangeDataMapper;
    private StorageRepository mockStorageRepository;

    @Before
    public void setUp() {
        restaurantDataMapper = mock(RestaurantDataMapper.class);
        photoDataMapper = mock(PhotoDataMapper.class);
        cuisineDataMapper = mock(CuisineDataMapper.class);
        userDataMapper = mock(UserDataMapper.class);
        commentDataMapper = mock(CommentDataMapper.class);
        likeDataMapper = mock(LikeDataMapper.class);
        priceRangeDataMapper = mock(PriceRangeDataMapper.class);
        mockStorageRepository = mock(StorageRepository.class);

        RestaurantsController restaurantsController = new RestaurantsController(
                restaurantDataMapper,
                photoDataMapper,
                cuisineDataMapper,
                userDataMapper,
                commentDataMapper,
                likeDataMapper,
                priceRangeDataMapper,
                mockStorageRepository
        );

        mockMvc = standaloneSetup(restaurantsController)
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();
    }

    @Test
    public void test_getAll_returnsAListOfRestaurants() throws Exception {
        List<Restaurant> restaurants = singletonList(
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
                )
        );
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1)));
        when(userDataMapper.findForUserIds(anyList()))
                .thenReturn(Arrays.asList(new User(1L, "taro@email.com", "taro")));
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(new PriceRange(1L, "100yen"))
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                asList(new Like(1L, 1L), new Like(2L, 1L))
        );
        when(cuisineDataMapper.getAll()).thenReturn(
                asList(new Cuisine(20L, "Swedish"))
        );


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
    public void test_getAll_returnsRestaurantsWithoutLikes() throws Exception {
        List<Restaurant> restaurants = singletonList(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "とても美味しい",
                        "created-date",
                        "updated-date",
                        1,
                        1L,
                        1L
                )
        );
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1)));
        when(userDataMapper.findForUserIds(anyList()))
                .thenReturn(Arrays.asList(new User(1L, "taro@email.com", "taro")));
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(new PriceRange(1L, "100yen"))
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                emptyList()
        );


        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].offers_english_menu", equalTo(false)))
                .andExpect(jsonPath("$[0].walk_ins_ok", equalTo(true)))
                .andExpect(jsonPath("$[0].accepts_credit_cards", equalTo(false)))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("http://www.cats.com/my-cat.jpg")))
                .andExpect(jsonPath("$[0].price_range", equalTo("100yen")))
                .andExpect(jsonPath("$[0].num_likes", equalTo(0)))
                .andExpect(jsonPath("$[0].created_by_user_name", equalTo("taro")));
    }

    @Test
    public void test_getAll_returnsEmptyListWhenNoRestaurants() throws Exception {
        RestaurantsController controller = new RestaurantsController(
                restaurantDataMapper,
                new PhotoDataMapper(new JdbcTemplate(buildDataSource())),
                cuisineDataMapper,
                userDataMapper,
                commentDataMapper,
                likeDataMapper,
                priceRangeDataMapper,
                mockStorageRepository
        );


        mockMvc = standaloneSetup(controller).build();
        when(restaurantDataMapper.getAll()).thenReturn(emptyList());

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void test_create_persistsARestaurant() throws Exception {
        ArgumentCaptor<NewRestaurant> attributeNewRestaurant = ArgumentCaptor.forClass(NewRestaurant.class);
        ArgumentCaptor<Long> attributeCreatedByUserId = ArgumentCaptor.forClass(Long.class);

        when(restaurantDataMapper.createRestaurant(
                attributeNewRestaurant.capture(),
                attributeCreatedByUserId.capture()
        )).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "soooo goood",
                        "2016-04-13 16:01:21.094",
                        "2016-04-14 16:01:21.094",
                        99,
                        1L,
                        2L
                )
        );

        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(singletonList(new PhotoUrl(999, "http://some-url", 1)));
        when(cuisineDataMapper.getCuisine("2")).thenReturn(
                Optional.of(
                        new Cuisine(
                                2,
                                "Ramen"
                        )
                )
        );
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(
                        new User(99L, "jiro@mail.com", "jiro")
                )
        );
        when(priceRangeDataMapper.getPriceRange(anyLong())).thenReturn(
                Optional.of(
                        new PriceRange(
                                1,
                                "~900"
                        )
                )
        );

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
                        .requestAttr("userId", 99L)
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
                .andExpect(jsonPath("$.created_at", equalTo("2016-04-13T16:01:21.094Z")))
                .andExpect(jsonPath("$.updated_at", equalTo("2016-04-14T16:01:21.094Z")))
                .andExpect(jsonPath("$.created_by_user_name", is("jiro")));

        assertEquals(99, attributeCreatedByUserId.getValue().longValue());
    }

    @Test
    public void test_create_persistsARestaurantWithoutACuisineId() throws Exception {
        when(restaurantDataMapper.createRestaurant(anyObject(), anyLong())).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "soooo goood",
                        "created-date",
                        "updated-date", 1,
                        0L,
                        0L
                )
        );

        Cuisine expectedCuisine = new Cuisine(0, "Not Specified");
        when(cuisineDataMapper.getCuisine("0")).thenReturn(
                Optional.of(expectedCuisine));
        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(singletonList(new PhotoUrl(999, "http://some-url", 1)));
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(
                        new User(1L, "jiro@mail.com", "jiro")
                )
        );
        when(priceRangeDataMapper.getPriceRange(anyLong())).thenReturn(
                Optional.of(
                        new PriceRange(0L, "Not Specified")
                )
        );

        String payload = "{\"restaurant\": " +
                "{\"name\":\"Afuri\", " +
                "\"address\": \"Roppongi\", " +
                "\"offers_english_menu\": false, " +
                "\"walk_ins_ok\": true, " +
                "\"accepts_credit_cards\": false, " +
                "\"notes\": \"soooo goood\"," +
                "\"photo_urls\": [{\"url\": \"http://some-url\"}]}" +
                "}";

        mockMvc.perform(
                post("/restaurants")
                        .requestAttr("userId", 1L)
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
                .andExpect(jsonPath("$.cuisine.name", is("Not Specified")))
                .andExpect(jsonPath("$.cuisine.id", is(0)))
                .andExpect(jsonPath("$.price_range", is("Not Specified")));
    }

    @Test
    public void test_create_withInvalidCuisineId() throws Exception {
        when(restaurantDataMapper.createRestaurant(anyObject(), anyLong())).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "soooo goood",
                        "created-date",
                        "updated-date", 1,
                        0L,
                        2L
                )
        );

        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(singletonList(new PhotoUrl(999, "http://some-url", 1)));
        when(cuisineDataMapper.getCuisine("2")).thenReturn(
                Optional.empty()
        );
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(
                        new User(1L, "jiro@mail.com", "jiro")
                )
        );
        when(priceRangeDataMapper.getPriceRange(anyLong())).thenReturn(
                Optional.of(
                        new PriceRange(0L, "Not Specified")
                )
        );

        String payload = "{\"restaurant\": " +
                "{\"name\":\"Afuri\", " +
                "\"address\": \"Roppongi\", " +
                "\"offers_english_menu\": false, " +
                "\"walk_ins_ok\": true, " +
                "\"accepts_credit_cards\": false, " +
                "\"notes\": \"soooo goood\"," +
                "\"photo_urls\": [{\"url\": \"http://some-url\"}], " +
                "\"cuisine_id\": \"2\"}" +
                "}";

        mockMvc.perform(
                post("/restaurants")
                        .requestAttr("userId", 1L)
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
                .andExpect(jsonPath("$.price_range", is("Not Specified")))
                .andExpect(jsonPath("$.cuisine", isEmptyOrNullString()));
    }

    @Test
    public void test_getRestaurant_returnsRestaurant() throws Exception {
        Restaurant afuriRestaurant = new Restaurant(
                1L,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                "2016-04-13 16:01:21.094",
                "2016-04-14 16:01:21.094",
                1L,
                0L,
                1L
        );
        Cuisine expectedCuisine = new Cuisine(1L, "Ramen");
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.of(afuriRestaurant)
        );
        when(photoDataMapper.findForRestaurant(afuriRestaurant)).thenReturn(
                emptyList()
        );
        when(cuisineDataMapper.findForRestaurant(afuriRestaurant)).thenReturn(
                expectedCuisine
        );
        User hanakoUser = new User(1L, "hanako@email", "hanako");
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(hanakoUser)
        );
        when(commentDataMapper.findForRestaurant(afuriRestaurant.getId())).thenReturn(
                singletonList(
                        new SerializedComment(
                                new Comment(
                                        99L,
                                        "comment-content",
                                        "2016-04-01 10:10:10.000000",
                                        1L,
                                        1L
                                ),
                                hanakoUser
                        )
                )
        );
        when(likeDataMapper.findForRestaurant(afuriRestaurant.getId())).thenReturn(
                asList(
                        new Like(11L, 1L),
                        new Like(12L, 1L)
                )
        );
        when(priceRangeDataMapper.findForRestaurant(anyObject())).thenReturn(
                new PriceRange(0L, "Not Specified")
        );

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Afuri")))
                .andExpect(jsonPath("$.cuisine.name", equalTo("Ramen")))
                .andExpect(jsonPath("$.photo_urls", equalTo(emptyList())))
                .andExpect(jsonPath("$.created_by_user_name", equalTo("hanako")))
                .andExpect(jsonPath("$.comments[0].id", equalTo(99)))
                .andExpect(jsonPath("$.comments[0].user.name", equalTo("hanako")))
                .andExpect(jsonPath("$.num_likes", equalTo(2)))
                .andExpect(jsonPath("$.created_at", equalTo("2016-04-13T16:01:21.094Z")))
                .andExpect(jsonPath("$.updated_at", equalTo("2016-04-14T16:01:21.094Z")))
                .andExpect(jsonPath("$.price_range", is("Not Specified")));
    }

    @Test
    public void test_getRestaurant_returnsRestaurantWithPhotos() throws Exception {
        Restaurant afuriRestaurant = new Restaurant(
                1,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                "created-date",
                "updated-date", 1,
                0L,
                0L
        );
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.of(afuriRestaurant)
        );
        when(photoDataMapper.findForRestaurant(afuriRestaurant)).thenReturn(
                asList(new PhotoUrl(1, "Url1", 1), new PhotoUrl(2, "Url2", 1))
        );
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(new User(1L, "hanako@email", "hanako"))
        );
        when(priceRangeDataMapper.findForRestaurant(anyObject())).thenReturn(
                new PriceRange(0, "Not Specified")
        );

        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Afuri")))
                .andExpect(jsonPath("$.photo_urls[0].url", equalTo("Url1")))
                .andExpect(jsonPath("$.photo_urls[1].url", equalTo("Url2")));
    }

    @Test
    public void test_getRestaurant_returnsRestaurantWithLikeStatus() throws Exception {
        Restaurant afuriRestaurant = new Restaurant(
                1,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                "created-date",
                "updated-date", 1,
                0L,
                0L
        );
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.of(afuriRestaurant)
        );
        when(photoDataMapper.findForRestaurant(afuriRestaurant)).thenReturn(
                emptyList()
        );
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(new User(1L, "hanako@email", "hanako"))
        );
        when(likeDataMapper.findForRestaurant(afuriRestaurant.getId())).thenReturn(
                singletonList(new Like(11L, 1L))
        );
        when(priceRangeDataMapper.findForRestaurant(anyObject())).thenReturn(
                new PriceRange(0L, "Not Specified")
        );

        mockMvc.perform(get("/restaurants/1")
                        .requestAttr("userId", 11L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked", equalTo(true)));
    }

    @Test
    public void test_getRestaurant_returnsRestaurantWithPriceRange() throws Exception {
        Restaurant afuriRestaurant = new Restaurant(
                1,
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                "created-date",
                "updated-date", 1,
                1L,
                0L
        );
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.of(afuriRestaurant)
        );
        when(photoDataMapper.findForRestaurant(afuriRestaurant)).thenReturn(
                emptyList()
        );
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(new User(1L, "hanako@email", "hanako"))
        );
        when(likeDataMapper.findForRestaurant(afuriRestaurant.getId())).thenReturn(
                singletonList(new Like(11L, 1L))
        );
        when(priceRangeDataMapper.findForRestaurant(afuriRestaurant)).thenReturn(
                new PriceRange(1, "~900")
        );

        mockMvc.perform(get("/restaurants/1")
                .requestAttr("userId", 11L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price_range", equalTo("~900")));
    }

    @Test
    public void test_getInvalidRestaurantId_throwsException() throws Exception {
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.empty()
        );


        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"error\":\"Invalid restaurant id.\"}"));
    }

    @Test
    public void test_update_updatesRestaurantInformation() throws Exception {
        Restaurant updatedRestaurant = new Restaurant(
                1,
                "Updated Name",
                "Updated Address",
                false,
                true,
                false,
                "",
                "",
                "updated-date", 99,
                0L,
                2L
        );

        when(restaurantDataMapper.updateRestaurant(anyLong(), anyObject())).thenReturn(
                updatedRestaurant
        );
        when(photoDataMapper.findForRestaurant(updatedRestaurant))
                .thenReturn(singletonList(new PhotoUrl(999, "http://some-url", 1)));
        when(cuisineDataMapper.getCuisine("2")).thenReturn(
                Optional.of(
                        new Cuisine(
                                2,
                                "Ramen"
                        )
                )
        );
        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(
                        new User(99L, "jiro@mail.com", "jiro")
                )
        );
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
                .andExpect(jsonPath("$.created_by_user_name", is("jiro")));
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
        verify(mockStorageRepository, times(1)).deleteFile("http://hoge/image.jpg");
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
        verify(mockStorageRepository, never()).deleteFile(anyString());
    }

}
