package com.tokyo.beach.restaurants.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    DatabaseUser create(String email, String password, String name);
    Optional<DatabaseUser> get(LogonCredentials credentials);
    Optional<DatabaseUser> get(long userId);

    List<DatabaseUser> findForUserIds(List<Long> ids);
}
