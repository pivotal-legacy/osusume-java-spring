package com.tokyo.beach.restaurant;

import com.tokyo.beach.comment.CommentFixture;
import com.tokyo.beach.restaurants.comment.CommentRepository;
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
    private CommentRepository commentRepository;
    private RestaurantRepository repository;

    @Before
    public void setUp() {
        restaurantDataMapper = mock(RestaurantDataMapper.class);
        photoDataMapper = mock(PhotoDataMapper.class);
        cuisineDataMapper = mock(CuisineDataMapper.class);
        userDataMapper = mock(UserDataMapper.class);
        likeDataMapper = mock(LikeDataMapper.class);
        priceRangeDataMapper = mock(PriceRangeDataMapper.class);
        commentRepository = mock(CommentRepository.class);
        repository = new RestaurantRepository(restaurantDataMapper, photoDataMapper, userDataMapper, priceRangeDataMapper, likeDataMapper, cuisineDataMapper, commentRepository);
    }

    @Test
    public void test_getAll_returnsAllRestaurants() {
        Long userId = 1L;
        Cuisine cuisine = new Cuisine(20L, "Swedish");
        PriceRange priceRange = new PriceRange(1L, "100yen");
        User user = new User(userId, "taro@email.com", "taro");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1)
                .withNearestStation("Roppongi Station")
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

        List<SerializedRestaurant> serializedRestaurants = repository.getAll(userId);

        assertThat(serializedRestaurants.size(), equalTo(1));

        SerializedRestaurant serializedRestaurant = serializedRestaurants.get(0);

        assertThat(serializedRestaurant.getId(), equalTo(1L));
        assertThat(serializedRestaurant.getNearestStation(), equalTo("Roppongi Station"));
        assertThat(serializedRestaurant.getCreatedByUser().getId(), equalTo(userId));
        assertThat(serializedRestaurant.getPhotoUrlList().size(), equalTo(1));
        assertThat(serializedRestaurant.getPhotoUrlList().get(0).getId(), equalTo(999L));
        assertThat(serializedRestaurant.getCuisine().getId(), equalTo(20L));
        assertThat(serializedRestaurant.getPriceRange().getId(), equalTo(1L));
        assertThat(serializedRestaurant.isCurrentUserLikesRestaurant(), equalTo(true));
        assertThat(serializedRestaurant.getNumberOfLikes(), equalTo(2L));
    }

    @Test
    public void test_getAll_returnsRestaurantsWithoutLikes() throws Exception {
        Restaurant restaurant = new RestaurantFixture().build();
        List<Restaurant> restaurants = singletonList(restaurant);
        when(restaurantDataMapper.getAll()).thenReturn(restaurants);
        when(photoDataMapper.findForRestaurants(anyObject())).thenReturn(emptyList());
        when(userDataMapper.findForUserIds(anyList())).thenReturn(emptyList());
        when(priceRangeDataMapper.getAll()).thenReturn(emptyList());
        when(likeDataMapper.findForRestaurants(restaurants)).thenReturn(emptyList());
        when(cuisineDataMapper.getAll()).thenReturn(emptyList());

        List<SerializedRestaurant> serializedRestaurants = repository.getAll(1L);

        assertThat(serializedRestaurants.size(), equalTo(1));

        SerializedRestaurant serializedRestaurant = serializedRestaurants.get(0);

        assertThat(serializedRestaurant.isCurrentUserLikesRestaurant(), equalTo(false));
        assertThat(serializedRestaurant.getNumberOfLikes(), equalTo(0L));
    }

    @Test
    public void test_getRestaurant_returnsRestaurant() throws Exception {
        Long userId = 1L;
        Cuisine cuisine = new Cuisine(1L, "Ramen");
        User user = new User(userId, "hanako@email", "hanako");
        List<SerializedComment> comments = singletonList(
                new SerializedComment(
                        new CommentFixture().withId(1).build(),
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
        when(commentRepository.findForRestaurant(restaurant.getId())).thenReturn(
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

        SerializedRestaurant serializedRestaurant = repository.get(restaurant.getId(), userId).get();

        assertThat(serializedRestaurant.getId(), equalTo(1L));
        assertThat(serializedRestaurant.getCreatedByUser().getId(), equalTo(userId));
        assertThat(serializedRestaurant.getPhotoUrlList().size(), equalTo(2));
        assertThat(serializedRestaurant.getPhotoUrlList().get(0).getId(), equalTo(1L));
        assertThat(serializedRestaurant.getCuisine().getId(), equalTo(1L));
        assertThat(serializedRestaurant.getPriceRange().getId(), equalTo(0L));
        assertThat(serializedRestaurant.isCurrentUserLikesRestaurant(), equalTo(true));
        assertThat(serializedRestaurant.getNumberOfLikes(), equalTo(2L));
        assertThat(serializedRestaurant.getComments().size(), equalTo(1));
        assertThat(serializedRestaurant.getComments().get(0).getId(), equalTo(1L));
    }

    @Test
    public void test_create_persistsARestaurant() throws Exception {
        Long userId = 99L;
        List<PhotoUrl> photoUrls = singletonList(new PhotoUrl(999, "http://some-url", 1));
        Cuisine cuisine = new Cuisine(2,"Ramen");
        User user = new User(userId, "jiro@mail.com", "jiro");
        PriceRange priceRange = new PriceRange(1, "~900");
        Restaurant restaurant = new RestaurantFixture()
                .withId(1)
                .withNearestStation("Roppongi Station")
                .withCuisine(cuisine)
                .withPriceRange(priceRange)
                .withUser(user)
                .build();
        NewRestaurant newRestaurant = new NewRestaurantFixture()
                .withName(restaurant.getName())
                .withAddress(restaurant.getAddress())
                .withNearestStation(restaurant.getNearestStation())
                .withNotes(restaurant.getNotes())
                .withCuisineId(cuisine.getId())
                .withPriceRangeId(priceRange.getId())
                .build();
        when(restaurantDataMapper.createRestaurant(newRestaurant, userId))
                .thenReturn(restaurant);
        when(photoDataMapper.createPhotosForRestaurant(anyLong(), anyListOf(NewPhotoUrl.class)))
                .thenReturn(photoUrls);
        when(cuisineDataMapper.findForRestaurant(restaurant.getId())).thenReturn(cuisine);

        when(userDataMapper.findForRestaurantId(restaurant.getId()))
                .thenReturn(user);
        when(priceRangeDataMapper.findForRestaurant(restaurant.getId())).thenReturn(priceRange);

        SerializedRestaurant createdRestaurant = repository.create(newRestaurant, userId);

        assertThat(createdRestaurant.getId(), equalTo(1L));
        assertThat(createdRestaurant.getNearestStation(), equalTo("Roppongi Station"));
        assertThat(createdRestaurant.getCreatedByUser().getId(), equalTo(userId));
        assertThat(createdRestaurant.getPhotoUrlList().size(), equalTo(1));
        assertThat(createdRestaurant.getPhotoUrlList().get(0).getId(), equalTo(999L));
        assertThat(createdRestaurant.getCuisine().getId(), equalTo(2L));
        assertThat(createdRestaurant.getPriceRange().getId(), equalTo(1L));
        assertThat(createdRestaurant.isCurrentUserLikesRestaurant(), equalTo(false));
        assertThat(createdRestaurant.getNumberOfLikes(), equalTo(0L));
        assertThat(createdRestaurant.getComments().size(), equalTo(0));
    }

    @Test
    public void update_persistsTheRestaurant_andReturnsIt() {
        Cuisine cuisine = new Cuisine(2, "Ramen");
        PriceRange priceRange = new PriceRange(1, "900");
        User user = new User(99L, "jiro@mail.com", "jiro");
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
                .withPhotoUrls(emptyList())
                .build();

        List<SerializedComment> comments = singletonList(
                new SerializedComment(
                        new CommentFixture().withId(1).build(),
                        user
                )
        );

        when(restaurantDataMapper.updateRestaurant(restaurant.getId(), newRestaurant)).thenReturn(
                restaurant
        );
        when(photoDataMapper.createPhotosForRestaurant(restaurant.getId(), asList()))
                .thenReturn(asList());
        when(photoDataMapper.findForRestaurant(restaurant.getId()))
                .thenReturn(asList());
        when(cuisineDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                cuisine
        );
        when(priceRangeDataMapper.findForRestaurant(restaurant.getId()))
                .thenReturn(priceRange);
        when(userDataMapper.findForRestaurantId(restaurant.getId()))
                .thenReturn(user);
        when(commentRepository.findForRestaurant(restaurant.getId())).thenReturn(
                comments
        );
        when(likeDataMapper.findForRestaurant(restaurant.getId())).thenReturn(
                asList(
                        new Like(user.getId(), restaurant.getId()),
                        new Like(12L, restaurant.getId())
                )
        );


        SerializedRestaurant updatedRestaurant = repository.update(restaurant.getId(), newRestaurant);
        assertThat(updatedRestaurant.getId(), equalTo(1L));
        assertThat(updatedRestaurant.getCreatedByUser().getId(), equalTo(99L));
        assertThat(updatedRestaurant.getPhotoUrlList().size(), equalTo(0));
        assertThat(updatedRestaurant.getCuisine().getId(), equalTo(2L));
        assertThat(updatedRestaurant.getPriceRange().getId(), equalTo(1L));
        assertThat(updatedRestaurant.isCurrentUserLikesRestaurant(), equalTo(true));
        assertThat(updatedRestaurant.getNumberOfLikes(), equalTo(2L));
        assertThat(updatedRestaurant.getComments().size(), equalTo(1));
        assertThat(updatedRestaurant.getComments().get(0).getId(), equalTo(1L));
    }

    @Test
    public void update_persistsTheRestaurant_addsOnlyNewPhotoUrls() {
        long originalRestaurantId = 1;
        Cuisine cuisine = new Cuisine(2, "Ramen");
        PriceRange priceRange = new PriceRange(1, "900");
        User user = new User(99L, "jiro@mail.com", "jiro");

        NewRestaurant newRestaurant = new NewRestaurantFixture()
                .withPhotoUrls(asList(
                        new NewPhotoUrl("http://existing-url-one"),
                        new NewPhotoUrl("http://existing-url-two"),
                        new NewPhotoUrl("http://new-url-one"),
                        new NewPhotoUrl("http://new-url-two")
                ))
                .build();

        Restaurant existingRestaurant = new RestaurantFixture()
                .withId(1)
                .build();

        when(photoDataMapper.findForRestaurant(originalRestaurantId))
                .thenReturn(asList(
                        new PhotoUrl(999, "http://existing-url-one", 1),
                        new PhotoUrl(999, "http://existing-url-two", 1)
                ));

        when(photoDataMapper.createPhotosForRestaurant(originalRestaurantId, asList(
                new NewPhotoUrl("http://new-url-one"),
                new NewPhotoUrl("http://new-url-two")
        )))
                .thenReturn(asList(
                        new PhotoUrl(999, "http://new-url-one", 1),
                        new PhotoUrl(999, "http://new-url-two", 1)
                ));

        when(cuisineDataMapper.findForRestaurant(existingRestaurant.getId())).thenReturn(
                cuisine
        );
        when(priceRangeDataMapper.findForRestaurant(existingRestaurant.getId()))
                .thenReturn(priceRange);
        when(userDataMapper.findForRestaurantId(existingRestaurant.getId()))
                .thenReturn(user);

        when(restaurantDataMapper.updateRestaurant(originalRestaurantId, newRestaurant))
                .thenReturn(existingRestaurant);

        SerializedRestaurant updatedSerializedRestaurant = repository.update(existingRestaurant.getId(), newRestaurant);
        assertThat(
                updatedSerializedRestaurant.getPhotoUrlList(),
                equalTo(asList(
                        new PhotoUrl(999, "http://existing-url-one", 1),
                        new PhotoUrl(999, "http://existing-url-two", 1),
                        new PhotoUrl(999, "http://new-url-one", 1),
                        new PhotoUrl(999, "http://new-url-two", 1)
                ))
        );
    }
}
