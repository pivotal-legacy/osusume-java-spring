package com.tokyo.beach.application.restaurant;

import com.tokyo.beach.application.photos.PhotoUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/restaurants")
public class RestaurantsController {
    private final RestaurantRepository restaurantRepository;
    private final DetailedRestaurantRepository detailedRestaurantRepository;
    private final PhotoRepository photoRepository;

    @Autowired
    public RestaurantsController(
            RestaurantRepository restaurantRepo,
            DetailedRestaurantRepository detailedRepo,
            PhotoRepository photoRepository
    ) {
        this.restaurantRepository = restaurantRepo;
        this.detailedRestaurantRepository = detailedRepo;
        this.photoRepository = photoRepository;
    }

    @RequestMapping(value = "", method = GET)
    public List<SerializedRestaurant> getAll() {
        List<Restaurant> restaurantList = restaurantRepository.getAll();

        if (restaurantList.size() == 0) {
            return emptyList();
        }

        List<PhotoUrl> photos = photoRepository.findForRestaurants(restaurantList);
        Map<Integer, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        return restaurantList
                .stream()
                .map((restaurant) -> new SerializedRestaurant(
                        restaurant,
                        restaurantPhotos.get(restaurant.getId()
                        )
                ))
                .collect(toList());
    }

    @RequestMapping(value = "", method = POST)
    @ResponseStatus(CREATED)
    public SerializedRestaurant create(@RequestBody NewRestaurantWrapper restaurantWrapper) {
        Restaurant restaurant = restaurantRepository.createRestaurant(
                restaurantWrapper.getRestaurant()
        );

        List<PhotoUrl> photosForRestaurant = photoRepository.createPhotosForRestaurant(
                restaurant.getId(),
                restaurantWrapper.getPhotoUrls()
        );

        return new SerializedRestaurant(restaurant, photosForRestaurant);
    }

    @RequestMapping(value = "{id}", method = GET)
    public Restaurant getRestaurant(@PathVariable String id) {
        return detailedRestaurantRepository.getRestaurant(id);
    }
}
