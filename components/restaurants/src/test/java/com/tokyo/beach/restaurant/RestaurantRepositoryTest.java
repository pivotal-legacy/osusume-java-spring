package com.tokyo.beach.restaurant;

import com.tokyo.beach.comment.CommentFixture;
import com.tokyo.beach.restaurants.comment.CommentDataMapper;
import com.tokyo.beach.restaurants.comment.SerializedComment;
import com.tokyo.beach.restaurants.cuisine.Cuisine;
import com.tokyo.beach.restaurants.cuisine.CuisineDataMapper;
import com.tokyo.beach.restaurants.like.Like;
import com.tokyo.beach.restaurants.like.LikeDataMapper;
import com.tokyo.beach.restaurants.photos.NewPhotoUrl;
import com.tokyo.beach.restaurants.photos.PhotoDataMapper;
import com.tokyo.beach.restaurants.photos.PhotoUrl;
import com.tokyo.beach.restaurants.pricerange.PriceRange;
import com.tokyo.beach.restaurants.pricerange.PriceRangeDataMapper;
import com.tokyo.beach.restaurants.restaurant.*;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestaurantRepositoryTest {
    private RestaurantDataMapper restaurantDataMapper;
    private PhotoDataMapper photoDataMapper;
    private CuisineDataMapper cuisineDataMapper;
    private UserDataMapper userDataMapper;
    private LikeDataMapper likeDataMapper;
    private PriceRangeDataMapper priceRangeDataMapper;
    private CommentDataMapper commentDataMapper;
    private RestaurantRepository repository;

    @Before
    public void setUp() {
        restaurantDataMapper = mock(RestaurantDataMapper.class);
        photoDataMapper = mock(PhotoDataMapper.class);
        cuisineDataMapper = mock(CuisineDataMapper.class);
        userDataMapper = mock(UserDataMapper.class);
        likeDataMapper = mock(LikeDataMapper.class);
        priceRangeDataMapper = mock(PriceRangeDataMapper.class);
        commentDataMapper = mock(CommentDataMapper.class);
        repository = new RestaurantRepository(restaurantDataMapper, photoDataMapper, userDataMapper, priceRangeDataMapper, likeDataMapper, cuisineDataMapper, commentDataMapper);
    }

    @Test
    public void test_getAll_returnsAllRestaurants() {
        Long userId = 1L;
        Cuisine cuisine = new Cuisine(20L, "Swedish");
        PriceRange priceRange = new PriceRange(1L, "100yen");
        User user = new User(userId, "taro@email.com", "taro");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1)
                .withCuisine(cuisine)
                .withPriceRange(priceRange)
                .withUser(user)
                .build();
        List<Restaurant> restaurants = singletonList(
              restaurant
        );
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        List<PhotoUrl> photoUrls = singletonList(
                new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", restaurant.getId())
        );
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(photoUrls);
        when(userDataMapper.findForUserIds(anyList()))
                .thenReturn(Arrays.asList(user));
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(priceRange)
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                asList(new Like(1L, 1L), new Like(2L, 1L))
        );
        when(cuisineDataMapper.getAll()).thenReturn(
                asList(cuisine)
        );

        List<SerializedRestaurant> serializedRestaurants = singletonList(
                new SerializedRestaurant(restaurant, photoUrls, cuisine, priceRange, user, emptyList(), true, 2)
        );

        assertThat(repository.getAll(userId), equalTo(serializedRestaurants));
    }

    @Test
    public void test_getAll_returnsRestaurantsWithoutLikes() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "taro@email.com", "taro");
        PriceRange priceRange = new PriceRange(1L, "100yen");
        Cuisine cuisine = new Cuisine(20L, "Swedish");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1)
                .withCuisine(cuisine)
                .withPriceRange(priceRange)
                .withUser(user)
                .build();
        List<Restaurant> restaurants = singletonList(
                restaurant
        );
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://www.cats.com/my-cat.jpg", 1));
        when(photoDataMapper.findForRestaurants(anyObject()))
                .thenReturn(photoUrls);
        when(userDataMapper.findForUserIds(anyList()))
                .thenReturn(Arrays.asList(user));
        when(priceRangeDataMapper.getAll()).thenReturn(
                asList(priceRange)
        );
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(
                emptyList()
        );
        when(cuisineDataMapper.getAll()).thenReturn(
                asList(cuisine)
        );

        List<SerializedRestaurant> serializedRestaurants = singletonList(
                new SerializedRestaurant(restaurant, photoUrls, cuisine, priceRange, user, emptyList(), false, 0)
        );

        assertThat(repository.getAll(userId), equalTo(serializedRestaurants));
    }

    @Test
    public void test_getRestaurant_returnsRestaurant() throws Exception {
        Long userId = 1L;
        Cuisine cuisine = new Cuisine(1L, "Ramen");
        User user = new User(userId, "hanako@email", "hanako");
        List<SerializedComment> comments = singletonList(
                new SerializedComment(
                        new CommentFixture().build(),
                        user
                )
        );
        PriceRange priceRange = new PriceRange(0L, "Not Specified");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1)
                .withCuisine(cuisine)
                .withPriceRange(priceRange)
                .withUser(user)
                .build();
        List<PhotoUrl> photoUrls = asList(new PhotoUrl(1, "Url1", 1), new PhotoUrl(2, "Url2", 1));

        when(userDataMapper.findForRestaurantId(restaurant.getId()))
                .thenReturn(user);
        when(restaurantDataMapper.get(1)).thenReturn(
                Optional.of(restaurant)
        );
        when(cuisineDataMapper.findForRestaurant(restaurant.getId())).thenReturn(cuisine);
        when(commentDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                comments
        );
        when(likeDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                asList(
                        new Like(1L, 1L),
                        new Like(12L, 1L)
                )
        );
        when(priceRangeDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                priceRange
        );
        when(photoDataMapper.findForRestaurant(restaurant.getId())).thenReturn(photoUrls);

        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant,
                photoUrls,
                cuisine,
                priceRange,
                user,
                comments,
                true,
                2L
        );

        assertThat(repository.get(restaurant.getId(), userId).get(),
                equalTo(serializedRestaurant));
    }

    @Test
    public void test_create_persistsARestaurant() throws Exception {
        Long userId = 99L;
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://some-url", 1));
        Cuisine cuisine = new Cuisine(2,"Ramen");
        User user = new User(99L, "jiro@mail.com", "jiro");
        PriceRange priceRange = new PriceRange(1, "~900");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1)
                .withCuisine(cuisine)
                .withPriceRange(priceRange)
                .withUser(user)
                .build();
        NewRestaurant newRestaurant = new NewRestaurantFixture()
                .withName(restaurant.getName())
                .withAddress(restaurant.getAddress())
                .withNotes(restaurant.getNotes())
                .withCuisineId(cuisine.getId())
                .withPriceRangeId(priceRange.getId())
                .build();

        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                restaurant,
                photoUrls,
                cuisine,
                priceRange,
                user,
                emptyList(),
                false,
                0L
        );
        when(restaurantDataMapper.createRestaurant(newRestaurant, userId))
                .thenReturn(restaurant);
        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(photoUrls);
        when(cuisineDataMapper.findForRestaurant(restaurant.getId())).thenReturn(cuisine);

        when(userDataMapper.findForRestaurantId(restaurant.getId()))
                .thenReturn(user);
        when(priceRangeDataMapper.findForRestaurant(restaurant.getId())).thenReturn(priceRange);

        assertThat(repository.create(newRestaurant, userId),
                equalTo(serializedRestaurant));
    }

    @Test
    public void update_persistsTheRestaurant_andReturnsIt() {
        Cuisine cuisine = new Cuisine(2, "Ramen");
        PriceRange priceRange = new PriceRange(1, "900");
        User user = new User(99L, "jiro@mail.com", "jiro");
        Restaurant updatedRestaurant = new RestaurantFixture()
                .withId(1)
                .withCuisine(cuisine)
                .withPriceRange(priceRange)
                .withUser(user)
                .build();
        NewRestaurant newRestaurant = new NewRestaurantFixture()
                .withName(updatedRestaurant.getName())
                .withAddress(updatedRestaurant.getAddress())
                .withNotes(updatedRestaurant.getNotes())
                .withCuisineId(cuisine.getId())
                .withPriceRangeId(priceRange.getId())
                .build();
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://some-url", 1));
        List<SerializedComment> comments = singletonList(
                new SerializedComment(
                        new CommentFixture().build(),
                        user
                )
        );
        SerializedRestaurant serializedRestaurant = new SerializedRestaurant(
                updatedRestaurant,
                photoUrls,
                cuisine,
                priceRange,
                user,
                comments,
                true,
                2L
        );

        when(restaurantDataMapper.updateRestaurant(updatedRestaurant.getId(), newRestaurant)).thenReturn(
                updatedRestaurant
        );
        when(photoDataMapper.findForRestaurant(updatedRestaurant.getId()))
                .thenReturn(photoUrls);
        when(cuisineDataMapper.findForRestaurant(updatedRestaurant.getId())).thenReturn(
                cuisine
        );
        when(priceRangeDataMapper.findForRestaurant(updatedRestaurant.getId()))
                .thenReturn(priceRange);
        when(userDataMapper.findForRestaurantId(updatedRestaurant.getId()))
                .thenReturn(user);
        when(commentDataMapper.findForRestaurant(updatedRestaurant.getId())).thenReturn(
                comments
        );
        when(likeDataMapper.findForRestaurant(updatedRestaurant.getId())).thenReturn(
                asList(
                        new Like(user.getId(), updatedRestaurant.getId()),
                        new Like(12L, updatedRestaurant.getId())
                )
        );
        assertThat(repository.update(updatedRestaurant.getId(), newRestaurant),
                equalTo(serializedRestaurant));
    }
}
