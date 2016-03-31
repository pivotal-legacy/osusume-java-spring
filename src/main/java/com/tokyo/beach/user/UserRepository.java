package com.tokyo.beach.user;

public interface UserRepository {
    DatabaseUser logon(String email, String password);
    DatabaseUser create(String email, String password);
}
