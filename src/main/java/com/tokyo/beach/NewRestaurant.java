package com.tokyo.beach;

public class NewRestaurant {

    private String name;

    public NewRestaurant(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public NewRestaurant() {}

    public String getName() {
        return name;
    }
}
