package com.tokyo.beach.restaurants.session;

public class WrappedToken {

    private String token;

    public WrappedToken() {}

    public WrappedToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WrappedToken that = (WrappedToken) o;

        return token != null ? token.equals(that.token) : that.token == null;

    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "WrappedToken{" +
                "token='" + token + '\'' +
                '}';
    }
}
