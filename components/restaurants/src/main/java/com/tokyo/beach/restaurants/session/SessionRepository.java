package com.tokyo.beach.restaurants.session;

import com.tokyo.beach.restaurants.user.User;

import java.util.Optional;

public interface SessionRepository {
    UserSession create(TokenGenerator generator, User user);
    Optional<Long> validateToken(String token);
    void delete(String token);
}
