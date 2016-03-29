package com.tokyo.beach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return restaurantRepository.getAll();
    }

    @RequestMapping(value="", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@RequestBody NewRestaurant newRestaurant) {
        return restaurantRepository.createRestaurant(newRestaurant);
    }
}
