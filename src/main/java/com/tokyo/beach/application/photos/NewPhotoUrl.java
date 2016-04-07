package com.tokyo.beach.application.photos;

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
}
