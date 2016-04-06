package com.tokyo.beach.application.user;

import java.util.Optional;

public interface UserRepository {
    DatabaseUser create(String email, String password, String name);
    Optional<DatabaseUser> get(LogonCredentials credentials);
}
