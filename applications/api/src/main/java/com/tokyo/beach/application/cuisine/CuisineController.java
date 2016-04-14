package com.tokyo.beach.application.cuisine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/cuisines")
public class CuisineController {
    private CuisineRepository cuisineRepository;

    @Autowired
    public CuisineController(CuisineRepository cuisineRepository) {
        this.cuisineRepository = cuisineRepository;
    }

    @RequestMapping(value="", method = GET)
    public List<Cuisine> getAll() {
        return cuisineRepository.getAll();
    }

    @RequestMapping(value="{id}", method=GET)
    public Cuisine getCuisine(@PathVariable String id) {
        return cuisineRepository.getCuisine(id).get();
    }

    @RequestMapping(value="", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Cuisine create(@RequestBody NewCuisine newCuisine) {
        return cuisineRepository.createCuisine(newCuisine);
    }
}
