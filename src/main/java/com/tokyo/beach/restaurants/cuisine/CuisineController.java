package com.tokyo.beach.restaurants.cuisine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/cuisines")
@CrossOrigin
public class CuisineController {
    private CuisineDataMapper cuisineDataMapper;

    @Autowired
    public CuisineController(CuisineDataMapper cuisineDataMapper) {
        this.cuisineDataMapper = cuisineDataMapper;
    }

    @RequestMapping(value="", method = GET)
    public List<Cuisine> getAll() {
        return cuisineDataMapper.getAll();
    }

    @RequestMapping(value="{id}", method=GET)
    public Cuisine getCuisine(@PathVariable String id) {
        return cuisineDataMapper.getCuisine(Long.parseLong(id)).get();
    }

    @RequestMapping(value="", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Cuisine create(@RequestBody NewCuisine newCuisine) {
        return cuisineDataMapper.createCuisine(newCuisine);
    }
}
