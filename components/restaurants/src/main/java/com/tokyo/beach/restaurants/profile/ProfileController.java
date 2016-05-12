package com.tokyo.beach.restaurants.profile;

import com.tokyo.beach.restaurants.cuisine.CuisineRepository;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeRepository;
import com.tokyo.beach.restaurants.photos.PhotoRepository;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeRepository;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantRepository;
import com.tokyo.beach.restaurants.restaurant.SerializedRestaurant;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ProfileController {
    private RestaurantRepository restaurantRepository;
    private PhotoRepository photoRepository;
    private CuisineRepository cuisineRepository;
    private UserRepository userRepository;
    private LikeRepository likeRepository;
    private PriceRangeRepository priceRangeRepository;

    @Autowired
    public ProfileController(
            RestaurantRepository restaurantRepository,
            PhotoRepository photoRepository,
            CuisineRepository cuisineRepository,
            UserRepository userRepository,
            LikeRepository likeRepository,
            PriceRangeRepository priceRangeRepository) {
        this.restaurantRepository = restaurantRepository;
        this.photoRepository = photoRepository;
        this.cuisineRepository = cuisineRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.priceRangeRepository = priceRangeRepository;
    }

    @RequestMapping(value = "/profile/posts", method = GET)
    public List<SerializedRestaurant> posts() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<User> maybeUser = userRepository.get(userId.longValue());

        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsPostedByUser(userId.longValue());

        if (restaurantList.size() == 0) {
            return emptyList();
        }

        List<PhotoUrl> photos = photoRepository.findForRestaurants(restaurantList);
        Map<Long, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        List<PriceRange> priceRangeList = priceRangeRepository.getAll();
        Map<Long, PriceRange> priceRangeMap = new HashMap<>();
        priceRangeList.forEach(priceRange -> priceRangeMap.put(priceRange.getId(), priceRange));

        List<Like> likes = likeRepository.findForRestaurants(restaurantList);
        Map<Long, List<Like>> restaurantLikes = likes
                .stream()
                .collect(groupingBy(Like::getRestaurantId));

        List<SerializedRestaurant> resultList =
                restaurantList.stream()
                        .map((restaurant) -> new SerializedRestaurant(
                                restaurant,
                                restaurantPhotos.get(restaurant.getId()),
                                cuisineRepository.findForRestaurant(restaurant),
                                Optional.of(priceRangeMap.get(restaurant.getPriceRangeId())),
                                maybeUser,
                                emptyList(),
                                false,
                                restaurantLikes.get(restaurant.getId()) == null ? 0 : restaurantLikes.get(restaurant.getId()).size())
                        )
                        .collect(toList());

        return resultList;
    }

    @RequestMapping(value = "/profile/likes", method = GET)
    public List<SerializedRestaurant> likes() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        List<Long> likedRestaurants = likeRepository.getLikesByUser(userId.longValue());

        if (likedRestaurants.size() == 0) {
            return emptyList();
        }

        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsByIds(likedRestaurants);

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

        List<PhotoUrl> photos = photoRepository.findForRestaurants(restaurantList);
        Map<Long, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        List<PriceRange> priceRangeList = priceRangeRepository.getAll();
        Map<Long, PriceRange> priceRangeMape = new HashMap<>();
        priceRangeList.forEach(priceRange -> priceRangeMape.put(priceRange.getId(), priceRange));

        List<Like> likes = likeRepository.findForRestaurants(restaurantList);
        Map<Long, List<Like>> restaurantLikes = likes
                .stream()
                .collect(groupingBy(Like::getRestaurantId));


        List<SerializedRestaurant> resultList =
                restaurantList.stream()
                        .map((restaurant) -> new SerializedRestaurant(
                                restaurant,
                                restaurantPhotos.get(restaurant.getId()),
                                cuisineRepository.findForRestaurant(restaurant),
                                Optional.of(priceRangeMape.get(restaurant.getPriceRangeId())),
                                Optional.of(createdByUsers.get(restaurant.getCreatedByUserId())),
                                emptyList(),
                                true,
                                restaurantLikes.get(restaurant.getId()) == null ? 0 : restaurantLikes.get(restaurant.getId()).size())
                        )
                        .collect(toList());

        return resultList;
    }
}
