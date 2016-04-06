package com.tokyo.beach.application.restaurant;

import com.tokyo.beach.application.RestControllerException;
import com.tokyo.beach.application.cuisine.Cuisine;
import com.tokyo.beach.application.cuisine.CuisineRepository;
import com.tokyo.beach.application.photos.PhotoUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final PhotoRepository photoRepository;
    private final CuisineRepository cuisineRepository;

    @Autowired
    public RestaurantsController(
            RestaurantRepository restaurantRepo,
            PhotoRepository photoRepository,
            CuisineRepository cuisineRepository
    ) {
        this.restaurantRepository = restaurantRepo;
        this.photoRepository = photoRepository;
        this.cuisineRepository = cuisineRepository;
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
                        restaurantPhotos.get(restaurant.getId()),
                        null
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

        Cuisine cuisine = null;
        if (restaurantWrapper.getCuisineId().isPresent()) {
            cuisine = cuisineRepository.getCuisine(
                    String.valueOf(restaurantWrapper.getCuisineId().get())
            ).get();
        }

        return new SerializedRestaurant(restaurant, photosForRestaurant, cuisine);
    }

    @RequestMapping(value = "{id}", method = GET)
    public SerializedRestaurant getRestaurant(@PathVariable String id) {
        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(Integer.parseInt(id));

        maybeRestaurant.orElseThrow(() -> new RestControllerException("Invalid restaurant id."));

        Restaurant retrievedRestaurant = maybeRestaurant.get();
        List<PhotoUrl> photosForRestaurant = photoRepository.findForRestaurant(retrievedRestaurant);
        return new SerializedRestaurant(retrievedRestaurant, photosForRestaurant, null);
    }
}
