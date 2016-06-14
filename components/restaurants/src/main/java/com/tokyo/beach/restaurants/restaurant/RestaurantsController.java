package com.tokyo.beach.restaurants.restaurant;

import com.tokyo.beach.restaurants.comment.CommentDataMapper;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import com.tokyo.beach.restaurants.s3.S3StorageRepository;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import com.tokyo.beach.restutils.RestControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@CrossOrigin
@RestController
@RequestMapping("/restaurants")
public class RestaurantsController {
    private RestaurantRepository restaurantRepository;
    private final RestaurantDataMapper restaurantDataMapper;
    private final PhotoDataMapper photoDataMapper;
    private final CuisineDataMapper cuisineDataMapper;
    private final UserDataMapper userDataMapper;
    private final CommentDataMapper commentDataMapper;
    private final LikeDataMapper likeDataMapper;
    private final PriceRangeDataMapper priceRangeDataMapper;
    private final S3StorageRepository s3StorageRepository;

    @Autowired
    public RestaurantsController(
            RestaurantRepository restaurantRepository,
            RestaurantDataMapper restaurantDataMapper,
            PhotoDataMapper photoDataMapper,
            CuisineDataMapper cuisineDataMapper,
            UserDataMapper userDataMapper,
            CommentDataMapper commentDataMapper,
            LikeDataMapper likeDataMapper,
            PriceRangeDataMapper priceRangeDataMapper,
            S3StorageRepository storageRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantDataMapper = restaurantDataMapper;
        this.photoDataMapper = photoDataMapper;
        this.cuisineDataMapper = cuisineDataMapper;
        this.userDataMapper = userDataMapper;
        this.commentDataMapper = commentDataMapper;
        this.likeDataMapper = likeDataMapper;
        this.priceRangeDataMapper = priceRangeDataMapper;
        this.s3StorageRepository = storageRepository;
    }

    @RequestMapping(value = "", method = GET)
    public List<SerializedRestaurant> getAll() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");
        return restaurantRepository.getAll(userId.longValue());
    }

    @RequestMapping(value = "", method = POST)
    @ResponseStatus(CREATED)
    public SerializedRestaurant create(@RequestBody NewRestaurantWrapper restaurantWrapper) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Restaurant restaurant = restaurantDataMapper.createRestaurant(
                restaurantWrapper.getRestaurant(), userId.longValue()
        );
        Optional<User> createdByUser = userDataMapper.get(restaurant.getCreatedByUserId());
        List<PhotoUrl> photosForRestaurant = photoDataMapper.createPhotosForRestaurant(
                restaurant.getId(),
                restaurantWrapper.getPhotoUrls()
        );
        Optional<Cuisine> maybeCuisine = cuisineDataMapper.getCuisine(restaurantWrapper.getCuisineId().toString());
        Optional<PriceRange> maybePriceRange = priceRangeDataMapper.getPriceRange(restaurantWrapper.getPriceRangeId());

        return new SerializedRestaurant(
                restaurant,
                photosForRestaurant,
                maybeCuisine.orElse(null),
                maybePriceRange,
                createdByUser,
                emptyList(),
                false,
                0L);
    }

    @RequestMapping(value = "{id}", method = GET)
    public SerializedRestaurant getRestaurant(@PathVariable String id) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<Restaurant> maybeRestaurant = restaurantDataMapper.get(Integer.parseInt(id));

        maybeRestaurant.orElseThrow(() -> new RestControllerException("Invalid restaurant id."));

        Restaurant retrievedRestaurant = maybeRestaurant.get();
        Optional<User> createdByUser = userDataMapper.get(
                retrievedRestaurant.getCreatedByUserId()
        );
        List<PhotoUrl> photosForRestaurant = photoDataMapper.findForRestaurant(retrievedRestaurant);
        Cuisine cuisineForRestaurant = cuisineDataMapper.findForRestaurant(retrievedRestaurant);
        PriceRange priceRange = priceRangeDataMapper.findForRestaurant(retrievedRestaurant);

        List<SerializedComment> comments = commentDataMapper.findForRestaurant(retrievedRestaurant.getId());

        List<Like> likes = likeDataMapper.findForRestaurant(retrievedRestaurant.getId());
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
        Restaurant restaurant = restaurantDataMapper.updateRestaurant(
                new Long(id),
                restaurantWrapper.getRestaurant()
        );
        Optional<User> createdByUser = userDataMapper.get(restaurant.getCreatedByUserId());
        List<PhotoUrl> photosForRestaurant = photoDataMapper.findForRestaurant(restaurant);
        Optional<Cuisine> maybeCuisine = cuisineDataMapper.getCuisine(restaurantWrapper.getCuisineId().toString());

        return new SerializedRestaurant(
                restaurant,
                photosForRestaurant,
                maybeCuisine.orElse(null),
                Optional.empty(),
                createdByUser,
                emptyList(),
                false,
                0L
        );
    }

    @RequestMapping(value = "{restaurantId}/photoUrls/{photoUrlId}", method = DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deletePhotoUrl(@PathVariable String restaurantId, @PathVariable String photoUrlId) {
        Optional<PhotoUrl> maybePhotoUrl = photoDataMapper.get(Long.parseLong(photoUrlId));

        if (maybePhotoUrl.isPresent()) {
            photoDataMapper.delete(Long.parseLong(photoUrlId));
            s3StorageRepository.deleteFile(maybePhotoUrl.get().getUrl());
        }

    }
}
