package com.tokyo.beach.application.token;

import com.tokyo.beach.application.session.TokenGenerator;

public class UserSession {

    private String email;
    private String token;

    @SuppressWarnings("unused")
    public UserSession() {}

    public UserSession(TokenGenerator tokenGenerator, String email) {
        this.token = tokenGenerator.nextToken();
        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "token='" + token + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
