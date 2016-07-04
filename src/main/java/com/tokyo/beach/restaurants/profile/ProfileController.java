package com.tokyo.beach.restaurants.profile;

import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import com.tokyo.beach.restaurants.restaurant.Restaurant;
import com.tokyo.beach.restaurants.restaurant.RestaurantDataMapper;
import com.tokyo.beach.restaurants.restaurant.SerializedRestaurant;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
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
    private RestaurantDataMapper restaurantDataMapper;
    private PhotoDataMapper photoDataMapper;
    private CuisineDataMapper cuisineDataMapper;
    private UserDataMapper userDataMapper;
    private LikeDataMapper likeDataMapper;
    private PriceRangeDataMapper priceRangeDataMapper;

    @Autowired
    public ProfileController(
            RestaurantDataMapper restaurantDataMapper,
            PhotoDataMapper photoDataMapper,
            CuisineDataMapper cuisineDataMapper,
            UserDataMapper userDataMapper,
            LikeDataMapper likeDataMapper,
            PriceRangeDataMapper priceRangeDataMapper) {
        this.restaurantDataMapper = restaurantDataMapper;
        this.photoDataMapper = photoDataMapper;
        this.cuisineDataMapper = cuisineDataMapper;
        this.userDataMapper = userDataMapper;
        this.likeDataMapper = likeDataMapper;
        this.priceRangeDataMapper = priceRangeDataMapper;
    }

    @RequestMapping(value = "/profile/posts", method = GET)
    public List<SerializedRestaurant> posts() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<User> maybeUser = userDataMapper.get(userId.longValue());

        List<Restaurant> restaurantList = restaurantDataMapper.getRestaurantsPostedByUser(userId.longValue());

        if (restaurantList.size() == 0) {
            return emptyList();
        }

        List<Long> ids = restaurantList.stream().map(Restaurant::getId).collect(toList());
        List<PhotoUrl> photos = photoDataMapper.findForRestaurants(ids);
        Map<Long, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        List<PriceRange> priceRangeList = priceRangeDataMapper.getAll();
        Map<Long, PriceRange> priceRangeMap = new HashMap<>();
        priceRangeList.forEach(priceRange -> priceRangeMap.put(priceRange.getId(), priceRange));

        List<Like> likes = likeDataMapper.findForRestaurants(restaurantList);
        Map<Long, List<Like>> restaurantLikes = likes
                .stream()
                .collect(groupingBy(Like::getRestaurantId));

        List<SerializedRestaurant> resultList =
                restaurantList.stream()
                        .map((restaurant) -> new SerializedRestaurant(
                                restaurant,
                                restaurantPhotos.get(restaurant.getId()),
                                cuisineDataMapper.findForRestaurant(restaurant.getId()),
                                priceRangeMap.get(restaurant.getPriceRangeId()),
                                maybeUser.get(),
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

        List<Long> likedRestaurants = likeDataMapper.getLikesByUser(userId.longValue());

        if (likedRestaurants.size() == 0) {
            return emptyList();
        }

        List<Restaurant> restaurantList = restaurantDataMapper.getRestaurantsByIds(likedRestaurants);

        List<User> userList = userDataMapper.findForUserIds(
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

        List<Long> ids = restaurantList.stream().map(Restaurant::getId).collect(toList());
        List<PhotoUrl> photos = photoDataMapper.findForRestaurants(ids);
        Map<Long, List<PhotoUrl>> restaurantPhotos = photos.stream()
                .collect(groupingBy(PhotoUrl::getRestaurantId));

        List<PriceRange> priceRangeList = priceRangeDataMapper.getAll();
        Map<Long, PriceRange> priceRangeMap = new HashMap<>();
        priceRangeList.forEach(priceRange -> priceRangeMap.put(priceRange.getId(), priceRange));

        List<Like> likes = likeDataMapper.findForRestaurants(restaurantList);
        Map<Long, List<Like>> restaurantLikes = likes
                .stream()
                .collect(groupingBy(Like::getRestaurantId));


        List<SerializedRestaurant> resultList =
                restaurantList.stream()
                        .map((restaurant) -> new SerializedRestaurant(
                                restaurant,
                                restaurantPhotos.get(restaurant.getId()),
                                cuisineDataMapper.findForRestaurant(restaurant.getId()),
                                priceRangeMap.get(restaurant.getPriceRangeId()),
                                createdByUsers.get(restaurant.getCreatedByUserId()),
                                emptyList(),
                                true,
                                restaurantLikes.get(restaurant.getId()) == null ? 0 : restaurantLikes.get(restaurant.getId()).size())
                        )
                        .collect(toList());

        return resultList;
    }
}
