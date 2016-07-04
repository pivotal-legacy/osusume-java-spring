package com.tokyo.beach.restaurants.session;

public class UserSession {
    private String name;
    private Long id;
    private String email;
    private String token;

    public UserSession(TokenGenerator tokenGenerator, String email, String name, Long id) {
        this.token = tokenGenerator.nextToken();
        this.email = email;
        this.name = name;
        this.id = id;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSession that = (UserSession) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        return token != null ? token.equals(that.token) : that.token == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
