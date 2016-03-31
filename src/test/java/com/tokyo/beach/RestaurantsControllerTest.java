package com.tokyo.beach;

import com.tokyo.beach.photourl.NewPhotoUrl;
import com.tokyo.beach.photourl.PhotoUrl;
import com.tokyo.beach.restaurant.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestaurantsControllerTest {
    RestaurantRepository mockRestaurantRepository;
    DetailedRestaurantRepository mockDetailedRestaurantRepository;
    RestaurantsController restaurantsController;
    MockMvc mockMvc;

    @Before
    public void setUp() {
        mockRestaurantRepository = mock(RestaurantRepository.class);
        mockDetailedRestaurantRepository = mock(DetailedRestaurantRepository.class);
        restaurantsController = new RestaurantsController(mockRestaurantRepository, mockDetailedRestaurantRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantsController)
                .build();
    }

    @Test
    public void testGettingAListOfRestaurants() throws Exception {
        when(mockRestaurantRepository.getAll()).thenReturn(
            Collections.singletonList(
                    new Restaurant(
                            1,
                            "Afuri",
                            "Roppongi",
                            false,
                            true,
                            false,
                            "",
                            null
                    )
            )
        );


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/restaurants"));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", equalTo(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", equalTo("Afuri")));
    }

    @Test
    public void testCreatingARestaurant() throws Exception {
        ArrayList<NewPhotoUrl> afuriNewPhotoUrls = new ArrayList<>();
        NewRestaurant afuriNewRestaurant = new NewRestaurant(
                "Afuri",
                "Roppongi",
                false,
                true,
                false,
                "",
                afuriNewPhotoUrls
        );
        when(mockRestaurantRepository.createRestaurant(afuriNewRestaurant)).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
                        null
                )
        );



        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/restaurants")
                .contentType("application/json")
                .content("{\"name\":\"Afuri\"}"));


        result.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testGetRestaurantWithoutPhotUrls() throws Exception {
        when(mockDetailedRestaurantRepository.getRestaurant("1")).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
                        new ArrayList<PhotoUrl>()
                )
        );


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/restaurants/1"));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo("Afuri")));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.photo_urls", equalTo(new ArrayList<PhotoUrl>())));
    }

    @Test
    public void testGetRestaurantWithPhotoUrls() throws Exception {
        ArrayList<PhotoUrl> photoUrls = new ArrayList<PhotoUrl>();
        photoUrls.add(new PhotoUrl(1, "Url1", 1));
        photoUrls.add(new PhotoUrl(2, "Url2", 1));
        when(mockDetailedRestaurantRepository.getRestaurant("1")).thenReturn(
                new Restaurant(
                        1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "",
                        photoUrls
                )
        );


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/restaurants/1"));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name", equalTo("Afuri")));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.photo_urls[0].url", equalTo("Url1")));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.photo_urls[1].url", equalTo("Url2")));

    }
}
