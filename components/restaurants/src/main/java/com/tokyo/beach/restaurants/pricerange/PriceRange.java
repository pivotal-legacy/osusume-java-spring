package com.tokyo.beach.restaurants.pricerange;

import java.util.Optional;

public class PriceRange {
    private long id;
    private String range;

    @SuppressWarnings("unused")
    public PriceRange() {}

    public PriceRange(long id, String range) {
        this.id = id;
        this.range = range;
    }

    public PriceRange(String range) {
        this.range = range;
    }

    public long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "PriceRange{" +
                "id=" + id +
                ", range='" + range + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceRange that = (PriceRange) o;

        //noinspection SimplifiableIfStatement
        if (id != that.id) return false;
        return range != null ? range.equals(that.range) : that.range == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (range != null ? range.hashCode() : 0);
        return result;
    }
}
