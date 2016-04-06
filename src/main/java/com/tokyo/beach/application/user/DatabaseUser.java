package com.tokyo.beach.application.user;

public class DatabaseUser {

    private long id;
    private String email;

    public DatabaseUser(long id, String email) {
        this.id = id;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseUser that = (DatabaseUser) o;

        if (id != that.id) return false;
        return email != null ? email.equals(that.email) : that.email == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DatabaseUser{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
