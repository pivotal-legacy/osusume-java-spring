package com.tokyo.beach.restaurants.photos;

public class NewPhotoUrl {
    private String url;

    @SuppressWarnings("unused")
    public NewPhotoUrl() {}

    public NewPhotoUrl(String url) {
        this.url = url;
    }


    @SuppressWarnings("WeakerAccess")
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "NewPhotoUrl{" +
                "url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewPhotoUrl that = (NewPhotoUrl) o;

        return url != null ? url.equals(that.url) : that.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
