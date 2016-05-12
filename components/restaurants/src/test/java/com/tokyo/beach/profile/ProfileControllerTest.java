package com.tokyo.beach.profile;

import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeRepository;
import com.tokyo.beach.restutils.RestControllerExceptionHandler;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineRepository;
import com.tokyo.beach.restaurants.like.LikeRepository;
import com.tokyo.beach.restaurants.photos.PhotoRepository;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.profile.ProfileController;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantRepository;
import com.tokyo.beach.restaurants.user.DatabaseUser;
import com.tokyo.beach.restaurants.user.UserRepository;
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
    private RestaurantRepository restaurantRepository;
    private MockMvc mockMvc;
    private PhotoRepository photoRepository;
    private CuisineRepository cuisineRepository;
    private UserRepository userRepository;
    private LikeRepository mockLikeRepository;
    private PriceRangeRepository mockPriceRangeRepository;

    @Before
    public void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        photoRepository = mock(PhotoRepository.class);
        cuisineRepository = mock(CuisineRepository.class);
        userRepository = mock(UserRepository.class);
        mockLikeRepository = mock(LikeRepository.class);
        mockPriceRangeRepository = mock(PriceRangeRepository.class);

        ProfileController profileController = new ProfileController(
                restaurantRepository,
                photoRepository,
                cuisineRepository,
                userRepository,
                mockLikeRepository,
                mockPriceRangeRepository
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
                        false,
                        true,
                        false,
                        "とても美味しい",
                        "created-date",
                        1,
                        1L
                )
        );

        when(userRepository.get(anyLong()))
                .thenReturn(Optional.of(new DatabaseUser(1L, "user-email", "username")));

        when(restaurantRepository.getRestaurantsPostedByUser(1L)).thenReturn(posts);

        when(photoRepository.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "photo-url", 1)));

        when(cuisineRepository.findForRestaurant(anyObject()))
                .thenReturn(new Cuisine(10L, "Japanese"));

        when(mockPriceRangeRepository.findForRestaurants(posts)).thenReturn(
                asList(new PriceRange(1L, "¥1000 ~ ¥2000", 1L))
        );
        when(mockLikeRepository.findForRestaurants(posts)).thenReturn(
                asList(new Like(1L, 1L), new Like(2L, 1L))
        );

        mockMvc.perform(get("/profile/posts")
                .requestAttr("userId", 1L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].accepts_credit_cards", equalTo(false)))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].cuisine.id", equalTo(10)))
                .andExpect(jsonPath("$[0].cuisine.name", equalTo("Japanese")))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].offers_english_menu", equalTo(false)))
                .andExpect(jsonPath("$[0].user.name", equalTo("username")))
                .andExpect(jsonPath("$[0].walk_ins_ok", equalTo(true)))
                .andExpect(jsonPath("$[0].price_range", Matchers.equalTo("¥1000 ~ ¥2000")))
                .andExpect(jsonPath("$[0].num_likes", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("photo-url")));
    }

    @Test
    public void test_getUserPostsWhenUserHasntPosted_returnsEmptyList() throws Exception {
        when(userRepository.get(anyLong()))
                .thenReturn(Optional.of(new DatabaseUser(1L, "user-email", "username")));

        when(restaurantRepository.getRestaurantsPostedByUser(1L)).thenReturn(emptyList());

        mockMvc.perform(get("/profile/posts")
                .requestAttr("userId", 1L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void test_getUserLikes_returnsRestaurantList() throws Exception {
        List<Restaurant> posts = singletonList(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "とても美味しい",
                        "created-date",
                        99,
                        1L
                )
        );
        List<Long> likesList = singletonList(1L);

        when(userRepository.findForUserIds(singletonList(99L)))
                .thenReturn(singletonList(new DatabaseUser(99L, "user-email", "username")));
        when(mockLikeRepository.getLikesByUser(99L))
                .thenReturn(likesList);
        when(restaurantRepository.getRestaurantsByIds(likesList))
                .thenReturn(posts);
        when(photoRepository.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "photo-url", 1)));
        when(cuisineRepository.findForRestaurant(anyObject()))
                .thenReturn(new Cuisine(10L, "Japanese"));
        when(mockPriceRangeRepository.findForRestaurants(posts)).thenReturn(
                asList(new PriceRange(1L, "¥1000 ~ ¥2000", 1L))
        );
        when(mockLikeRepository.findForRestaurants(posts)).thenReturn(
                asList(new Like(99L, 1L), new Like(98L, 1L))
        );

        mockMvc.perform(get("/profile/likes").
                requestAttr("userId", 99L)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].accepts_credit_cards", equalTo(false)))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].cuisine.id", equalTo(10)))
                .andExpect(jsonPath("$[0].cuisine.name", equalTo("Japanese")))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].offers_english_menu", equalTo(false)))
                .andExpect(jsonPath("$[0].user.name", equalTo("username")))
                .andExpect(jsonPath("$[0].walk_ins_ok", equalTo(true)))
                .andExpect(jsonPath("$[0].price_range", Matchers.equalTo("¥1000 ~ ¥2000")))
                .andExpect(jsonPath("$[0].num_likes", Matchers.equalTo(2)))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("photo-url")));
    }

    @Test
    public void test_getUserLikes_returnsEmptyListWhenNoLikes() throws Exception {
        when(mockLikeRepository.getLikesByUser(99L))
                .thenReturn(emptyList());

        mockMvc.perform(get("/profile/likes").
                requestAttr("userId", 99L)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        verify(restaurantRepository, times(0)).getRestaurantsByIds(anyList());
    }
}
