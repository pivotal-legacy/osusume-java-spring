package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.comment.Comment;
import com.tokyo.beach.restaurants.comment.CommentDataMapper;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import com.tokyo.beach.restaurants.restaurant.*;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class RestaurantRepositoryTest {
    private RestaurantDataMapper restaurantDataMapper;
    private PhotoDataMapper photoDataMapper;
    private CuisineDataMapper cuisineDataMapper;
    private UserDataMapper userDataMapper;
    private LikeDataMapper likeDataMapper;
    private PriceRangeDataMapper priceRangeDataMapper;
    private CommentDataMapper commentDataMapper;
    private RestaurantRepository repository;

    @Before
    public void setUp() {
        restaurantDataMapper = mock(RestaurantDataMapper.class);
        photoDataMapper = mock(PhotoDataMapper.class);
        cuisineDataMapper = mock(CuisineDataMapper.class);
        userDataMapper = mock(UserDataMapper.class);
        likeDataMapper = mock(LikeDataMapper.class);
        priceRangeDataMapper = mock(PriceRangeDataMapper.class);
        commentDataMapper = mock(CommentDataMapper.class);
        repository = new RestaurantRepository(restaurantDataMapper, photoDataMapper, userDataMapper, priceRangeDataMapper, likeDataMapper, cuisineDataMapper, commentDataMapper);
    }

    @Test
    public void test_getAll_returnsAllRestaurants() {
        Long userId = 1L;
        Restaurant restaurant = new Restaurant(
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
        );
        List<Restaurant> restaurants = singletonList(
              restaurant
        );
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1));
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(photoUrls);
        User user = new User(1L, "taro@email.com", "taro");
        when(userDataMapper.findForUserIds(anyList()))
                .thenReturn(Arrays.asList(user));
        PriceRange priceRange = new PriceRange(1L, "100yen");
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(priceRange)
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                asList(new Like(1L, 1L), new Like(2L, 1L))
        );
        Cuisine cuisine = new Cuisine(20L, "Swedish");
        when(cuisineDataMapper.getAll()).thenReturn(
                asList(cuisine)
        );

        List<SerializedRestaurant> serializedRestaurants = singletonList(
                new SerializedRestaurant(restaurant, photoUrls, cuisine, Optional.of(priceRange), Optional.of(user), emptyList(), true, 2)
        );

        assertThat(repository.getAll(userId), equalTo(serializedRestaurants));
    }

    @Test
    public void test_getAll_returnsRestaurantsWithoutLikes() throws Exception {
        Long userId = 1L;
        Restaurant restaurant = new Restaurant(
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
        );
        List<Restaurant> restaurants = singletonList(
                restaurant
        );
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1));
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(photoUrls);
        User user = new User(1L, "taro@email.com", "taro");
        when(userDataMapper.findForUserIds(anyList()))
                .thenReturn(Arrays.asList(user));
        PriceRange priceRange = new PriceRange(1L, "100yen");
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(priceRange)
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                emptyList()
        );
        Cuisine cuisine = new Cuisine(20L, "Swedish");
        when(cuisineDataMapper.getAll()).thenReturn(
                asList(cuisine)
        );

        List<SerializedRestaurant> serializedRestaurants = singletonList(
                new SerializedRestaurant(restaurant, photoUrls, cuisine, Optional.of(priceRange), Optional.of(user), emptyList(), false, 0)
        );

        assertThat(repository.getAll(userId), equalTo(serializedRestaurants));
    }

    @Test
    public void test_getRestaurant_returnsRestaurant() throws Exception {
        Long userId = 1L;
        Restaurant restaurant = new Restaurant(
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
        User hanakoUser = new User(1L, "hanako@email", "hanako");
        List<SerializedComment> comments = singletonList(
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
        );
        PriceRange priceRange = new PriceRange(0L, "Not Specified");
        List<PhotoUrl> photoUrls = asList(new PhotoUrl(1, "Url1", 1), new PhotoUrl(2, "Url2", 1));

        when(userDataMapper.get(anyLong())).thenReturn(
                Optional.of(hanakoUser)
        );
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.of(restaurant)
        );
        when(cuisineDataMapper.findForRestaurant(restaurant)).thenReturn(
                expectedCuisine
        );
        when(commentDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                comments
        );
        when(likeDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                asList(
                        new Like(1L, 1L),
                        new Like(12L, 1L)
                )
        );
        when(priceRangeDataMapper.findForRestaurant(anyObject())).thenReturn(
                priceRange
        );
        when(photoDataMapper.findForRestaurant(restaurant)).thenReturn(photoUrls);

        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant,
                photoUrls,
                expectedCuisine,
                Optional.of(priceRange),
                Optional.of(hanakoUser),
                comments,
                true,
                2L
        );

        assertThat(repository.get(restaurant.getId(), userId).get(),
                equalTo(serializedRestaurant));
    }

    @Test
    public void test_create_persistsARestaurant() throws Exception {
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
        Restaurant restaurant = new Restaurant(
                1L,
                newRestaurant.getName(),
                newRestaurant.getAddress(),
                newRestaurant.getOffersEnglishMenu(),
                newRestaurant.getWalkInsOk(),
                newRestaurant.getAcceptsCreditCards(),
                newRestaurant.getNotes(),
                "2016-04-13 16:01:21.094",
                "2016-04-14 16:01:21.094",
                1L,
                0L,
                1L
        );
        Long userId = 99L;
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://some-url", 1));
        Cuisine expectedCuisine = new Cuisine(2,"Ramen");
        Optional<User> hanakoUser = Optional.of(new User(99L, "jiro@mail.com", "jiro"));
        Optional<PriceRange> priceRange = Optional.of(new PriceRange(1, "~900"));

        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant,
                photoUrls,
                expectedCuisine,
                priceRange,
                hanakoUser,
                emptyList(),
                false,
                0L
        );
        when(restaurantDataMapper.createRestaurant(newRestaurant, userId))
                .thenReturn(restaurant);
        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(photoUrls);
        when(cuisineDataMapper.getCuisine("2")).thenReturn(Optional.of(expectedCuisine));
        when(userDataMapper.get(anyLong())).thenReturn(hanakoUser);
        when(priceRangeDataMapper.getPriceRange(anyLong())).thenReturn(priceRange);

        assertThat(repository.create(newRestaurant, userId),
                equalTo(serializedRestaurant));
    }

    @Test
    public void test_create_persistsARestaurant_withoutACuisine() throws Exception {
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
        Restaurant restaurant = new Restaurant(
                1L,
                newRestaurant.getName(),
                newRestaurant.getAddress(),
                newRestaurant.getOffersEnglishMenu(),
                newRestaurant.getWalkInsOk(),
                newRestaurant.getAcceptsCreditCards(),
                newRestaurant.getNotes(),
                "2016-04-13 16:01:21.094",
                "2016-04-14 16:01:21.094",
                1L,
                0L,
                1L
        );
        Long userId = 99L;
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://some-url", 1));
        Optional<User> hanakoUser = Optional.of(new User(99L, "jiro@mail.com", "jiro"));
        Optional<PriceRange> priceRange = Optional.of(new PriceRange(1, "~900"));

        when(restaurantDataMapper.createRestaurant(newRestaurant, userId))
                .thenReturn(restaurant);
        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(photoUrls);
        when(cuisineDataMapper.getCuisine("2")).thenReturn(Optional.empty());
        when(userDataMapper.get(anyLong())).thenReturn(hanakoUser);
        when(priceRangeDataMapper.getPriceRange(anyLong())).thenReturn(priceRange);

        assertThat(repository.create(newRestaurant, userId).getCuisine(),
                equalTo(null));
    }
}
