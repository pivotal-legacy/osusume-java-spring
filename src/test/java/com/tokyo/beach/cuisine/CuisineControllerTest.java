package com.tokyo.beach.cuisine;

import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.cuisine.CuisineController;
import com.tokyo.beach.application.cuisine.DatabaseCuisineRepository;
import com.tokyo.beach.application.cuisine.NewCuisine;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CuisineControllerTest {
    DatabaseCuisineRepository mockCuisineRepository;
    CuisineController cuisineController;
    MockMvc mockMvc;

    @Before
    public void setup() {
        mockCuisineRepository = mock(DatabaseCuisineRepository.class);
        cuisineController = new CuisineController(mockCuisineRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(cuisineController).build();
    }
    @Test
    public void testGetAllCuisines() throws Exception {


        when(mockCuisineRepository.getAll()).thenReturn(
                Arrays.asList(
                        new Cuisine(1, "Japanese"),
                        new Cuisine(2, "Spanish")
                )
        );

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/cuisines"));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].id", equalTo(1)));
        result.andExpect(jsonPath("$[0].name", equalTo("Japanese")));
        result.andExpect(jsonPath("$[1].id", equalTo(2)));
        result.andExpect(jsonPath("$[1].name", equalTo("Spanish")));
    }

    @Test
    public void testGetCuisine() throws Exception {
        when(mockCuisineRepository.getCuisine("1")).thenReturn(
                new Cuisine(1, "Japanese")
        );

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/cuisines/1"));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id", equalTo(1)));
        result.andExpect(jsonPath("$.name", equalTo("Japanese")));
    }

    @Test
    public void testCreateACuisine() throws Exception {
        NewCuisine newCuisine = new NewCuisine("Japanese");
        when(mockCuisineRepository.createCuisine(newCuisine)).thenReturn(
                new Cuisine(
                        1,
                        "Japanese"
                )
        );

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/cuisines")
            .contentType("application/json")
            .content("{\"name\":\"Japanese\"}"));

        result.andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
