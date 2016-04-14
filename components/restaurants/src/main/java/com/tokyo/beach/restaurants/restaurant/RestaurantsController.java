package com.tokyo.beach.restaurants.restaurant;

import com.tokyo.beach.restutils.RestControllerException;
import com.tokyo.beach.restaurants.comment.CommentRepository;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineRepository;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeRepository;
import com.tokyo.beach.restaurants.photos.PhotoRepository;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.user.DatabaseUser;
import com.tokyo.beach.restaurants.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public RestaurantsController(
            RestaurantRepository restaurantRepo,
            PhotoRepository photoRepository,
            CuisineRepository cuisineRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            LikeRepository likeRepository) {
        this.restaurantRepository = restaurantRepo;
        this.photoRepository = photoRepository;
        this.cuisineRepository = cuisineRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    @RequestMapping(value = "", method = GET)
    public List<SerializedRestaurant> getAll() {
        List<Restaurant> restaurantList = restaurantRepository.getAll();

        if (restaurantList.size() == 0) {
            return emptyList();
        }

        List<PhotoUrl> photos = photoRepository.findForRestaurants(restaurantList);
        Map<Long, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        List<DatabaseUser> userList = userRepository.findForUserIds(
                restaurantList.stream()
                        .map(Restaurant::getCreatedByUserId)
                        .collect(toList())
        );
        Map<Long, DatabaseUser> createdByUsers = userList.stream()
                .collect(
                        Collectors.toMap(
                                DatabaseUser::getId, UnaryOperator.identity()
                        )
                );

        return restaurantList
                .stream()
                .map((restaurant) -> new SerializedRestaurant(
                        restaurant,
                        restaurantPhotos.get(restaurant.getId()),
                        null,
                        Optional.of(createdByUsers.get(restaurant.getCreatedByUserId())),
                        emptyList(),
                        false
                ))
                .collect(toList());
    }

    @RequestMapping(value = "", method = POST)
    @ResponseStatus(CREATED)
    public SerializedRestaurant create(@RequestBody NewRestaurantWrapper restaurantWrapper) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Restaurant restaurant = restaurantRepository.createRestaurant(
                restaurantWrapper.getRestaurant(), userId.longValue()
        );
        Optional<DatabaseUser> createdByUser = userRepository.get(restaurant.getCreatedByUserId());
        List<PhotoUrl> photosForRestaurant = photoRepository.createPhotosForRestaurant(
                restaurant.getId(),
                restaurantWrapper.getPhotoUrls()
        );
        Optional<Cuisine> maybeCuisine = cuisineRepository.getCuisine(restaurantWrapper.getCuisineId().toString());

        return new SerializedRestaurant(
                restaurant,
                photosForRestaurant,
                maybeCuisine.orElse(null),
                createdByUser,
                emptyList(),
                false
        );
    }

    @RequestMapping(value = "{id}", method = GET)
    public SerializedRestaurant getRestaurant(@PathVariable String id) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(Integer.parseInt(id));

        maybeRestaurant.orElseThrow(() -> new RestControllerException("Invalid restaurant id."));

        Restaurant retrievedRestaurant = maybeRestaurant.get();
        Optional<DatabaseUser> createdByUser = userRepository.get(
                retrievedRestaurant.getCreatedByUserId()
        );
        List<PhotoUrl> photosForRestaurant = photoRepository.findForRestaurant(retrievedRestaurant);
        Cuisine cuisineForRestaurant = cuisineRepository.findForRestaurant(retrievedRestaurant);

        List<SerializedComment> comments = commentRepository.findForRestaurant(retrievedRestaurant);

        List<Like> likes = likeRepository.findForRestaurant(retrievedRestaurant.getId());
        boolean currentUserLikesRestaurant = likes
                .stream()
                .map(Like::getUserId)
                .anyMatch(Predicate.isEqual(userId));

        return new SerializedRestaurant(
                retrievedRestaurant,
                photosForRestaurant,
                cuisineForRestaurant,
                createdByUser,
                comments,
                currentUserLikesRestaurant
        );
    }
}
