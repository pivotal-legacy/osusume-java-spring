package com.tokyo.beach.restaurants.user;

import com.tokyo.beach.restaurants.session.LogonCredentials;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(String email, String password, String name);
    Optional<User> get(LogonCredentials credentials);
    Optional<User> get(long userId);

    List<User> findForUserIds(List<Long> ids);
}
