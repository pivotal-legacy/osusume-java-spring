package com.tokyo.beach;

public class RestaurantsController {
    public Restaurant[] getAll() {
        Restaurant[] retValue = {new Restaurant(1, "Afuri")};
        return retValue;
    }
}
