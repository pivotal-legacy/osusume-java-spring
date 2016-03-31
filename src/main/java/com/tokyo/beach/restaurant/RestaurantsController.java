package com.tokyo.beach.restaurant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantsController {
    private final RestaurantRepository restaurantRepository;
    private final DetailedRestaurantRepository detailedRestaurantRepository;

    @Autowired
    public RestaurantsController(RestaurantRepository repo, DetailedRestaurantRepository detailedRepo) {
        this.restaurantRepository = repo;
        this.detailedRestaurantRepository = detailedRepo;
    }

    @RequestMapping(value="", method = RequestMethod.GET)
    public List<Restaurant> getAll() {
        return restaurantRepository.getAll();
    }

    @RequestMapping(value="", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant create(@RequestBody NewRestaurantWrapper restaurantWrapper) {
        return restaurantRepository.createRestaurant(restaurantWrapper.getRestaurant());
    }

    @RequestMapping(value="{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Restaurant getRestaurant(@PathVariable String id) {
        return detailedRestaurantRepository.getRestaurant(id);
    }
}
