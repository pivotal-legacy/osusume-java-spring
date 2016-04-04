package com.tokyo.beach.application.session;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSession that = (UserSession) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return token != null ? token.equals(that.token) : that.token == null;

    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "token='" + token + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
