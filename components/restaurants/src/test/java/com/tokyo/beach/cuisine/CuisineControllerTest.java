package com.tokyo.beach.cuisine;

import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineController;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.cuisine.NewCuisine;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CuisineControllerTest {
    CuisineDataMapper cuisineDataMapper;
    CuisineController cuisineController;
    MockMvc mockMvc;

    @Before
    public void setup() {
        cuisineDataMapper = mock(CuisineDataMapper.class);
        cuisineController = new CuisineController(cuisineDataMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(cuisineController).build();
    }

    @Test
    public void testGetAllCuisines() throws Exception {


        when(cuisineDataMapper.getAll()).thenReturn(
                Arrays.asList(
                        new Cuisine(1, "Japanese"),
                        new Cuisine(2, "Spanish")
                )
        );

        ResultActions result = mockMvc.perform(get("/cuisines"));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id", equalTo(1)));
        result.andExpect(jsonPath("$[0].name", equalTo("Japanese")));
        result.andExpect(jsonPath("$[1].id", equalTo(2)));
        result.andExpect(jsonPath("$[1].name", equalTo("Spanish")));
    }

    @Test
    public void testGetCuisine() throws Exception {
        when(cuisineDataMapper.getCuisine("1")).thenReturn(
                Optional.of(
                        new Cuisine(
                                1,
                                "Japanese"
                        )
                )
        );

        ResultActions result = mockMvc.perform(get("/cuisines/1"));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id", equalTo(1)));
        result.andExpect(jsonPath("$.name", equalTo("Japanese")));
    }

    @Test
    public void testCreateACuisine() throws Exception {
        NewCuisine newCuisine = new NewCuisine("Japanese");
        when(cuisineDataMapper.createCuisine(newCuisine)).thenReturn(
                new Cuisine(
                        1,
                        "Japanese"
                )
        );

        ResultActions result = mockMvc.perform(post("/cuisines")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content("{\"name\":\"Japanese\"}"));

        result.andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
