package com.tokyo.beach.application.session;

import java.util.Optional;

public interface SessionRepository {
    Optional<UserSession> create(TokenGenerator generator, String email, String password);
}
