package com.tokyo.beach;

import org.hamcrest.Matchers;
import org.junit.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.*;
import org.springframework.test.web.servlet.request.*;
import org.springframework.test.web.servlet.result.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

public class RestaurantsControllerTest {
    @Test
    public void testGettingAListOfRestaurants() throws Exception {
        RestaurantRepository mockRestaurantRepository = mock(RestaurantRepository.class);
        when(mockRestaurantRepository.getAll()).thenReturn(
            Arrays.asList(
                    new Restaurant(1, "Afuri")
            )
        );
        RestaurantsController restaurantsController = new RestaurantsController(mockRestaurantRepository);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restaurantsController)
                .build();


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/restaurants"));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.equalTo(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.equalTo("Afuri")));
    }

    @Test
    public void testCreatingARestaurant() throws Exception {
        RestaurantRepository mockRestaurantRepository = mock(RestaurantRepository.class);
        NewRestaurant afuriNewRestaurant = new NewRestaurant("Afuri");
        when(mockRestaurantRepository.createRestaurant(afuriNewRestaurant)).thenReturn(
                new Restaurant(1, "Afuri")
        );
        RestaurantsController restaurantsController = new RestaurantsController(mockRestaurantRepository);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(restaurantsController)
                .build();


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/restaurants")
                .content(afuriNewRestaurant.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

//        .content(newUser.toString()) // <-- sets the request content !
//                .accept(MediaType.APPLICATION_JSON)
//                .andExpect(status().isOk());



    }
}
