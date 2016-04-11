package com.tokyo.beach.profile;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.cuisine.CuisineRepository;
import com.tokyo.beach.application.photos.PhotoRepository;
import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.application.profile.ProfileController;
import com.tokyo.beach.application.restaurant.Restaurant;
import com.tokyo.beach.application.restaurant.RestaurantRepository;
import com.tokyo.beach.application.restaurant.RestaurantsController;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.tokyo.beach.ControllerTestingUtils.createControllerAdvice;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ProfileControllerTest {
    private RestaurantRepository restaurantRepository;
    private MockMvc mockMvc;
    private PhotoRepository photoRepository;
    private CuisineRepository cuisineRepository;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        photoRepository = mock(PhotoRepository.class);
        cuisineRepository = mock(CuisineRepository.class);
        userRepository = mock(UserRepository.class);

        ProfileController profileController= new ProfileController(
                restaurantRepository,
                photoRepository,
                cuisineRepository,
                userRepository
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
                        1
                )
        );

        when(userRepository.get(anyLong()))
                .thenReturn(Optional.of(new DatabaseUser(1L, "user-email", "username")));

        when(restaurantRepository.getRestaurantsPostedByUser(1L)).thenReturn(posts);

        when(photoRepository.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "photo-url", 1)));

        when(cuisineRepository.findForRestaurant(anyObject()))
                .thenReturn(new Cuisine(10L, "Japanese"));

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

}
