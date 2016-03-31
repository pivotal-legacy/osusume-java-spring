package com.tokyo.beach.application.logon;

public class LogonCredentials {

    private String email;
    private String password;

    public LogonCredentials() {}

    public LogonCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LogonCredentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
