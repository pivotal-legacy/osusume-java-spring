package com.tokyo.beach.application.user;

import java.util.Optional;

public interface UserRepository {
    DatabaseUser create(String email, String password);
    Optional<DatabaseUser> get(LogonCredentials credentials);
}
