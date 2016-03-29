package com.tokyo.beach;

public class Restaurant {
    private final int id;
    private final String name;

    public Restaurant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @SuppressWarnings("unused")
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass()) return false;

        Restaurant restaurant = (Restaurant)o;
        return id == restaurant.id && (name != null ? name.equals(restaurant.name) : restaurant.name == null);
    }
}
