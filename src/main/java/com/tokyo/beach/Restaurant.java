package com.tokyo.beach;

public class Restaurant {
    private final int id;
    private final String name;

    public Restaurant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
