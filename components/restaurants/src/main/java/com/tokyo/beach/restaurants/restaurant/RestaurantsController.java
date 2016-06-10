package com.tokyo.beach.restaurants.restaurant;

import com.tokyo.beach.restaurants.comment.CommentRepository;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineRepository;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeRepository;
import com.tokyo.beach.restaurants.photos.PhotoRepository;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeRepository;
import com.tokyo.beach.restaurants.s3.StorageRepository;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserRepository;
import com.tokyo.beach.restutils.RestControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@CrossOrigin
@RestController
@RequestMapping("/restaurants")
public class RestaurantsController {
    private final RestaurantRepository restaurantRepository;
    private final PhotoRepository photoRepository;
    private final CuisineRepository cuisineRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PriceRangeRepository priceRangeRepository;
    private final StorageRepository storageRepository;

    @Autowired
    public RestaurantsController(
            RestaurantRepository restaurantRepo,
            PhotoRepository photoRepository,
            CuisineRepository cuisineRepository,
            UserRepository userRepository,
            CommentRepository commentRepository,
            LikeRepository likeRepository,
            PriceRangeRepository priceRangeRepository,
            StorageRepository storageRepository
    ) {
        this.restaurantRepository = restaurantRepo;
        this.photoRepository = photoRepository;
        this.cuisineRepository = cuisineRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.priceRangeRepository = priceRangeRepository;
        this.storageRepository = storageRepository;
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

        List<User> userList = userRepository.findForUserIds(
                restaurantList.stream()
                        .map(Restaurant::getCreatedByUserId)
                        .collect(toList())
        );
        Map<Long, User> createdByUsers = userList.stream()
                .collect(
                        Collectors.toMap(
                                User::getId, UnaryOperator.identity()
                        )
                );
        List<PriceRange> priceRangeList = priceRangeRepository.getAll();
        Map<Long, PriceRange> priceRangeMap = new HashMap<>();
        priceRangeList.forEach(priceRange -> priceRangeMap.put(priceRange.getId(), priceRange));

        List<Cuisine> cuisineList = cuisineRepository.getAll();
        Map<Long, Cuisine> cuisineMap = new HashMap<>();
        cuisineList.forEach(cuisine -> cuisineMap.put(cuisine.getId(), cuisine));

        List<Like> likes = likeRepository.findForRestaurants(restaurantList);
        Map<Long, List<Like>> restaurantLikes = likes
                .stream()
                .collect(groupingBy(Like::getRestaurantId));

        return restaurantList
                .stream()
                .map((restaurant) -> new SerializedRestaurant(
                        restaurant,
                        restaurantPhotos.get(restaurant.getId()),
                        cuisineMap.get(restaurant.getCuisineId()),
                        Optional.of(priceRangeMap.get(restaurant.getPriceRangeId())),
                        Optional.of(createdByUsers.get(restaurant.getCreatedByUserId())),
                        emptyList(),
                        false,
                        restaurantLikes.get(restaurant.getId()) == null ? 0 : restaurantLikes.get(restaurant.getId()).size()
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
        Optional<User> createdByUser = userRepository.get(restaurant.getCreatedByUserId());
        List<PhotoUrl> photosForRestaurant = photoRepository.createPhotosForRestaurant(
                restaurant.getId(),
                restaurantWrapper.getPhotoUrls()
        );
        Optional<Cuisine> maybeCuisine = cuisineRepository.getCuisine(restaurantWrapper.getCuisineId().toString());
        Optional<PriceRange> maybePriceRange = priceRangeRepository.getPriceRange(restaurantWrapper.getPriceRangeId());

        return new SerializedRestaurant(
                restaurant,
                photosForRestaurant,
                maybeCuisine.orElse(null),
                maybePriceRange,
                createdByUser);
    }

    @RequestMapping(value = "{id}", method = GET)
    public SerializedRestaurant getRestaurant(@PathVariable String id) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<Restaurant> maybeRestaurant = restaurantRepository.get(Integer.parseInt(id));

        maybeRestaurant.orElseThrow(() -> new RestControllerException("Invalid restaurant id."));

        Restaurant retrievedRestaurant = maybeRestaurant.get();
        Optional<User> createdByUser = userRepository.get(
                retrievedRestaurant.getCreatedByUserId()
        );
        List<PhotoUrl> photosForRestaurant = photoRepository.findForRestaurant(retrievedRestaurant);
        Cuisine cuisineForRestaurant = cuisineRepository.findForRestaurant(retrievedRestaurant);
        PriceRange priceRange = priceRangeRepository.findForRestaurant(retrievedRestaurant);

        List<SerializedComment> comments = commentRepository.findForRestaurant(retrievedRestaurant.getId());

        List<Like> likes = likeRepository.findForRestaurant(retrievedRestaurant.getId());
        boolean currentUserLikesRestaurant = likes
                .stream()
                .map(Like::getUserId)
                .anyMatch(Predicate.isEqual(userId));

        return new SerializedRestaurant(
                retrievedRestaurant,
                photosForRestaurant,
                cuisineForRestaurant,
                Optional.of(priceRange),
                createdByUser,
                comments,
                currentUserLikesRestaurant,
                likes.size()
        );
    }

    @RequestMapping(value = "{id}", method = PATCH)
    @ResponseStatus(OK)
    public SerializedRestaurant updateRestaurant(
            @PathVariable String id,
            @RequestBody NewRestaurantWrapper restaurantWrapper
    ) {
        Restaurant restaurant = restaurantRepository.updateRestaurant(
                new Long(id),
                restaurantWrapper.getRestaurant()
        );
        Optional<User> createdByUser = userRepository.get(restaurant.getCreatedByUserId());
        List<PhotoUrl> photosForRestaurant = photoRepository.findForRestaurant(restaurant);
        Optional<Cuisine> maybeCuisine = cuisineRepository.getCuisine(restaurantWrapper.getCuisineId().toString());

        return new SerializedRestaurant(
                restaurant,
                photosForRestaurant,
                maybeCuisine.orElse(null),
                Optional.empty(),
                createdByUser
        );
    }

    @RequestMapping(value = "{restaurantId}/photoUrls/{photoUrlId}", method = DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deletePhotoUrl(@PathVariable String restaurantId, @PathVariable String photoUrlId) {
        Optional<PhotoUrl> maybePhotoUrl = photoRepository.get(Long.parseLong(photoUrlId));

        if (maybePhotoUrl.isPresent()) {
            photoRepository.delete(Long.parseLong(photoUrlId));
            storageRepository.deleteFile(maybePhotoUrl.get().getUrl());
        }

    }
}
