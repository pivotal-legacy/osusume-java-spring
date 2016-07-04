package com.tokyo.beach.pricerange;

import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeController;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PriceRangeControllerTest {
    MockMvc mockMvc;
    PriceRangeController priceRangeController;
    PriceRangeDataMapper priceRangeDataMapper;

    @Before
    public void setUp() throws Exception {
        priceRangeDataMapper = mock(PriceRangeDataMapper.class);
        when(priceRangeDataMapper.getAll()).thenReturn(
                Arrays.asList(
                        new PriceRange(1, "¥0-999"),
                        new PriceRange(2, "¥1000-1999")
                )
        );

        priceRangeController = new PriceRangeController(priceRangeDataMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(priceRangeController)
                .build();
    }

    @Test
    public void test_getAllPriceRanges_returnsPriceRangeJson() throws Exception {
        ResultActions result = mockMvc.perform(get("/priceranges"));


        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].range", equalTo("¥0-999")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].range", equalTo("¥1000-1999")));
    }
}
