package com.tokyo.beach.application.session;

import java.util.Optional;

public interface SessionRepository {
    Optional<UserSession> logon(TokenGenerator generator, String email, String password);
}
