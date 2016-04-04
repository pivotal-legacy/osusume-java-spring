package com.tokyo.beach.application.user;

import com.tokyo.beach.application.logon.LogonCredentials;

public interface UserRepository {
    DatabaseUser create(String email, String password);
    DatabaseUser get(LogonCredentials credentials);
}
