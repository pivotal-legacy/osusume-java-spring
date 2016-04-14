package com.tokyo.beach.restaurants.session;

import com.tokyo.beach.restaurants.user.DatabaseUser;

import java.util.Optional;

public interface SessionRepository {
    UserSession create(TokenGenerator generator, DatabaseUser user);
    Optional<Long> validateToken(String token);
    void delete(String token);
}
