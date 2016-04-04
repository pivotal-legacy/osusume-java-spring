package com.tokyo.beach.restaurant;

import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.application.restaurant.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class RestaurantsControllerTest {
    private RestaurantRepository restaurantRepository;
    private DetailedRestaurantRepository mockDetailedRestaurantRepository;
    private MockMvc mockMvc;
    private PhotoRepository photoRepository;

    @Before
    public void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        mockDetailedRestaurantRepository = mock(DetailedRestaurantRepository.class);
        photoRepository = mock(PhotoRepository.class);

        RestaurantsController restaurantsController = new RestaurantsController(
                restaurantRepository,
                mockDetailedRestaurantRepository,
                photoRepository
        );

        mockMvc = standaloneSetup(restaurantsController).build();
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
                        emptyList()
                )
        );
        when(restaurantRepository.getAll()).thenReturn(restaurants);
        when(photoRepository.findForRestaurants(anyObject()))
                .thenReturn(singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1)));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Afuri")))
                .andExpect(jsonPath("$[0].address", equalTo("Roppongi")))
                .andExpect(jsonPath("$[0].offers_english_menu", equalTo(false)))
                .andExpect(jsonPath("$[0].walk_ins_ok", equalTo(true)))
                .andExpect(jsonPath("$[0].accepts_credit_cards", equalTo(false)))
                .andExpect(jsonPath("$[0].notes", equalTo("とても美味しい")))
                .andExpect(jsonPath("$[0].photo_urls[0].url", equalTo("http://www.cats.com/my-cat.jpg")));
    }

    @Test
    public void testCreatingARestaurant() throws Exception {
        NewRestaurant afuriNewRestaurant = new NewRestaurant(
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                emptyList()
        );
        when(restaurantRepository.createRestaurant(afuriNewRestaurant)).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
                        emptyList()
                )
        );


        mockMvc.perform(
                post("/restaurants")
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .content("{\"name\":\"Afuri\"}")
        )
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetRestaurantWithoutPhotoUrls() throws Exception {
        when(mockDetailedRestaurantRepository.getRestaurant("1")).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
                        emptyList()
                )
        );


        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Afuri")))
                .andExpect(jsonPath("$.photo_urls", equalTo(emptyList())));
    }

    @Test
    public void testGetRestaurantWithPhotoUrls() throws Exception {
        when(mockDetailedRestaurantRepository.getRestaurant("1")).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
                        asList(new PhotoUrl(1, "Url1", 1), new PhotoUrl(2, "Url2", 1))
                )
        );


        mockMvc.perform(get("/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Afuri")))
                .andExpect(jsonPath("$.photo_urls[0].url", equalTo("Url1")))
                .andExpect(jsonPath("$.photo_urls[1].url", equalTo("Url2")));

    }
}
