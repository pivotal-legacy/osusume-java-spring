package com.tokyo.beach;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestaurantsControllerTest {
    RestaurantRepository mockRestaurantRepository;
    RestaurantsController restaurantsController;
    MockMvc mockMvc;

    @Before
    public void setUp() {
        mockRestaurantRepository = mock(RestaurantRepository.class);
        restaurantsController = new RestaurantsController(mockRestaurantRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantsController)
                .build();
    }

    @Test
    public void testGettingAListOfRestaurants() throws Exception {
        when(mockRestaurantRepository.getAll()).thenReturn(
            Collections.singletonList(
                    new Restaurant(1,
                            "Afuri",
                            "Roppongi",
                            false,
                            true,
                            false,
                            "")
            )
        );


        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/restaurants"));


        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.equalTo(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.equalTo("Afuri")));
    }

    @Test
    public void testCreatingARestaurant() throws Exception {
        NewRestaurant afuriNewRestaurant = new NewRestaurant("Afuri",
                "Roppongi",
                false,
                true,
                false,
                "");

        when(mockRestaurantRepository.createRestaurant(afuriNewRestaurant)).thenReturn(
                new Restaurant(1,
                        "Afuri",
                        "Roppongi",
                        false,
                        true,
                        false,
                        "")
        );



        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/restaurants")
                .contentType("application/json")
                .content("{\"name\":\"Afuri\"}"));


        result.andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
