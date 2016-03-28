package com.tokyo.beach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantsController {
    private final RestaurantRepository restaurantRepository;
    @Autowired
    public RestaurantsController(RestaurantRepository repo) {
        this.restaurantRepository = repo;
    }

    @RequestMapping(value="", method = RequestMethod.GET)
    public List<Restaurant> getAll() {
        return restaurantRepository.selectAll();
    }
}
