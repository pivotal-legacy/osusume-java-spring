package com.tokyo.beach.restaurant;

import com.tokyo.beach.restaurants.comment.CommentDataMapper;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
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
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.TestDatabaseUtils.buildDataSource;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Before
    public void setUp() {
        restaurantDataMapper = mock(RestaurantDataMapper.class);
        photoDataMapper = mock(PhotoDataMapper.class);
        cuisineDataMapper = mock(CuisineDataMapper.class);
        userDataMapper = mock(UserDataMapper.class);
        likeDataMapper = mock(LikeDataMapper.class);
        priceRangeDataMapper = mock(PriceRangeDataMapper.class);
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

        RestaurantRepository repository = new RestaurantRepository(restaurantDataMapper, photoDataMapper, userDataMapper, priceRangeDataMapper, likeDataMapper, cuisineDataMapper);
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

        RestaurantRepository repository = new RestaurantRepository(restaurantDataMapper, photoDataMapper, userDataMapper, priceRangeDataMapper, likeDataMapper, cuisineDataMapper);
        assertThat(repository.getAll(userId), equalTo(serializedRestaurants));
    }
}
