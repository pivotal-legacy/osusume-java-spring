package com.tokyo.beach.application.profile;

import com.tokyo.beach.application.cuisine.CuisineRepository;
import com.tokyo.beach.application.photos.PhotoRepository;
import com.tokyo.beach.application.photos.PhotoUrl;
import com.tokyo.beach.application.restaurant.Restaurant;
import com.tokyo.beach.application.restaurant.RestaurantRepository;
import com.tokyo.beach.application.restaurant.SerializedRestaurant;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ProfileController {
    private RestaurantRepository restaurantRepository;
    private PhotoRepository photoRepository;
    private CuisineRepository cuisineRepository;
    private UserRepository userRepository;

    @Autowired
    public ProfileController(RestaurantRepository restaurantRepository, PhotoRepository photoRepository, CuisineRepository cuisineRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.photoRepository = photoRepository;
        this.cuisineRepository = cuisineRepository;
        this.userRepository = userRepository;
    }

    public RestaurantRepository getRestaurantRepository() {
        return restaurantRepository;
    }

    public PhotoRepository getPhotoRepository() {
        return photoRepository;
    }

    public CuisineRepository getCuisineRepository() {
        return cuisineRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @RequestMapping(value="/profile/posts", method = GET)
    public List<SerializedRestaurant> posts()
    {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<DatabaseUser> maybeUser = userRepository.get(userId.longValue());

        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsPostedByUser(userId.longValue());

        if (restaurantList.size() == 0) {
            return emptyList();
        }

        List<PhotoUrl> photos = photoRepository.findForRestaurants(restaurantList);
        Map<Long, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        List<SerializedRestaurant> resultList =
                restaurantList.stream()
                .map((restaurant) -> new SerializedRestaurant(
                        restaurant,
                        restaurantPhotos.get(restaurant.getId()),
                        cuisineRepository.findForRestaurant(restaurant),
                        maybeUser,
                        emptyList()
                ))
                .collect(toList());
        System.out.println("resultList = " + resultList);
        for (SerializedRestaurant restaurant: resultList) {
            System.out.println("restaurant = " + restaurant);
        }

        return resultList;
    }


}
