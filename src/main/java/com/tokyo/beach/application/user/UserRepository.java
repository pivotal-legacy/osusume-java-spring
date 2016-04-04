package com.tokyo.beach.application.user;

public interface UserRepository {
    DatabaseUser create(String email, String password);
}
