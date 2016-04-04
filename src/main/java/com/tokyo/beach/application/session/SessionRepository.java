package com.tokyo.beach.application.session;

import com.tokyo.beach.application.user.DatabaseUser;

public interface SessionRepository {
    UserSession create(TokenGenerator generator, DatabaseUser user);
}
