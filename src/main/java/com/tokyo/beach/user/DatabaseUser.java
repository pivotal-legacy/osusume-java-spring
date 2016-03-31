package com.tokyo.beach.user;

public class DatabaseUser {

    private Number id;
    private String username;
    private String password;

    public DatabaseUser(Number id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Number getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DatabaseUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
