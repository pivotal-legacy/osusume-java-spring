package com.tokyo.beach.application.session;

import com.tokyo.beach.application.user.DatabaseUser;

import java.util.Optional;

public interface SessionRepository {
    UserSession create(TokenGenerator generator, DatabaseUser user);
    Optional<Number> validateToken(String token);
    void delete(String token);
}
