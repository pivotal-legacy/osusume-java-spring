package com.tokyo.beach.user;

public class DatabaseUser {

    private Number id;
    private String email;

    public DatabaseUser(Number id, String email) {
        this.id = id;
        this.email = email;
    }

    public Number getId() {
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

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return email != null ? email.equals(that.email) : that.email == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
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
