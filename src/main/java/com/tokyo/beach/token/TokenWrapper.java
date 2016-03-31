package com.tokyo.beach.token;

import com.tokyo.beach.session.TokenGenerator;

public class TokenWrapper {

    private String token;

    public TokenWrapper() {}

    public TokenWrapper(TokenGenerator tokenGenerator) {
        this.token = tokenGenerator.nextToken();
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "TokenWrapper{" +
                "token='" + token + '\'' +
                '}';
    }
}
