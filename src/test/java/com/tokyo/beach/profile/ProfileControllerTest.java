package com.tokyo.beach.profile;

import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import com.tokyo.beach.restaurants.profile.ProfileController;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantDataMapper;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import com.tokyo.beach.restutils.RestControllerExceptionHandler;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.restutils.ControllerTestingUtils.createControllerAdvice;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ProfileControllerTest {
    private RestaurantDataMapper restaurantDataMapper;
    private MockMvc mockMvc;
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

        ProfileController profileController = new ProfileController(
                restaurantDataMapper,
                photoDataMapper,
                cuisineDataMapper,
                userDataMapper,
                likeDataMapper,
                priceRangeDataMapper
        );

        mockMvc = standaloneSetup(profileController)
                .setControllerAdvice(createControllerAdvice(new RestControllerExceptionHandler()))
                .build();
    }

    @Test
    public void test_getUserPosts_returnsRestaurantsPostedByUser() throws Exception {
        List<Restaurant> posts = singletonList(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        "Roppongi Station",
                        "some-place-id",
                        1.23,
                        2.34,
                        "とても美味しい",
                        "created-date",
                        "updated-date", 1,
                        1L,
                        10L
                )
        );

        when(userDataMapper.get(anyLong()))
                .thenReturn(Optional.of(new User(1L, "user-email", "username")));

        when(restaurantDataMapper.getRestaurantsPostedByUser(1L)).thenReturn(posts);

        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "photo-url", 1)));

        when(cuisineDataMapper.findForRestaurant(1))
                .thenReturn(new Cuisine(10L, "Japanese"));

        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(new PriceRange(1L, "¥1000 ~ ¥2000"))
        );
        when(likeDataMapper.findForRestaurants(posts)).thenReturn(
                asList(new Like(1L, 1L), new Like(2L, 1L))
        );

        mockMvc.perform(get("/profile/posts")
                .requestAttr("userId", 1L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].place_id", equalTo("some-place-id")))
                .andExpect(jsonPath("$[0].cuisine.id", equalTo(10)))
                .andExpect(jsonPath("$[0].cuisine.name", equalTo("Japanese")))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].user.name", equalTo("username")))
                .andExpect(jsonPath("$[0].price_range.id", equalTo(1)))
                .andExpect(jsonPath("$[0].price_range.range", Matchers.equalTo("¥1000 ~ ¥2000")))
                .andExpect(jsonPath("$[0].num_likes", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("photo-url")));
    }

    @Test
    public void test_getUserPostsWhenUserHasntPosted_returnsEmptyList() throws Exception {
        when(userDataMapper.get(anyLong()))
                .thenReturn(Optional.of(new User(1L, "user-email", "username")));

        when(restaurantDataMapper.getRestaurantsPostedByUser(1L)).thenReturn(emptyList());

        mockMvc.perform(get("/profile/posts")
                .requestAttr("userId", 1L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void test_getUserLikes_returnsRestaurantList() throws Exception {
        List<Restaurant> restaurants = singletonList(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        "Roppongi Station",
                        "some-place-id",
                        1.23,
                        2.34,
                        "とても美味しい",
                        "created-date",
                        "updated-date", 99,
                        1L,
                        10L
                )
        );
        List<Long> likesList = singletonList(1L);

        when(userDataMapper.findForUserIds(singletonList(99L)))
                .thenReturn(singletonList(new User(99L, "user-email", "username")));
        when(likeDataMapper.getLikesByUser(99L))
                .thenReturn(likesList);
        when(restaurantDataMapper.getRestaurantsByIds(likesList))
                .thenReturn(restaurants);
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "photo-url", 1)));
        when(cuisineDataMapper.findForRestaurant(1))
                .thenReturn(new Cuisine(10L, "Japanese"));
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(new PriceRange(1L, "¥1000 ~ ¥2000"))
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                asList(new Like(99L, 1L), new Like(98L, 1L))
        );

        mockMvc.perform(get("/profile/likes").
                requestAttr("userId", 99L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].place_id", equalTo("some-place-id")))
                .andExpect(jsonPath("$[0].cuisine.id", equalTo(10)))
                .andExpect(jsonPath("$[0].cuisine.name", equalTo("Japanese")))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].user.name", equalTo("username")))
                .andExpect(jsonPath("$[0].price_range.id", equalTo(1)))
                .andExpect(jsonPath("$[0].price_range.range", Matchers.equalTo("¥1000 ~ ¥2000")))
                .andExpect(jsonPath("$[0].num_likes", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("photo-url")));
    }

    @Test
    public void test_getUserLikes_returnsEmptyListWhenNoLikes() throws Exception {
        when(likeDataMapper.getLikesByUser(99L))
                .thenReturn(emptyList());

        mockMvc.perform(get("/profile/likes").
                requestAttr("userId", 99L)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        verify(restaurantDataMapper, times(0)).getRestaurantsByIds(anyList());
    }
}
