package com.tokyo.beach;

import java.util.List;

@SuppressWarnings("unused")

public class Restaurant {
    private final int id;
    private final String name;
    private String address;
    private Boolean offersEnglishMenu;
    private Boolean walkInsOk;
    private Boolean acceptsCreditCards;
    private String notes;
    private List<PhotoUrl> photoUrlList;

    public Restaurant(int id,
                      String name,
                      String address,
                      Boolean offersEnglishMenu,
                      Boolean walkInsOk,
                      Boolean acceptsCreditCards,
                      String notes,
                      List<PhotoUrl> photoUrlList) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.offersEnglishMenu = offersEnglishMenu;
        this.walkInsOk = walkInsOk;
        this.acceptsCreditCards = acceptsCreditCards;
        this.notes = notes;
        this.photoUrlList = photoUrlList;
    }

    public int getId() {
        return id;
    }
    public Boolean getOffersEnglishMenu() {
        return offersEnglishMenu;
    }

    public Boolean getWalkInsOk() {
        return walkInsOk;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getAcceptsCreditCards() {
        return acceptsCreditCards;
    }

    public String getNotes() {
        return notes;
    }


    public List<PhotoUrl> getPhotoUrlList() {
        return photoUrlList;
    }

    public void setPhotoUrlList(List<PhotoUrl> photoUrlList) {
        this.photoUrlList = photoUrlList;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass()) return false;

        Restaurant restaurant = (Restaurant)o;
        return id == restaurant.id &&
                (name != null ? name.equals(restaurant.name) : restaurant.name == null) &&
                (address != null ? address.equals(restaurant.address) : restaurant.address == null) &&
                (offersEnglishMenu != null ? offersEnglishMenu.equals(restaurant.offersEnglishMenu) : restaurant.offersEnglishMenu == null) &&
                (walkInsOk != null ? walkInsOk.equals(restaurant.walkInsOk) : restaurant.walkInsOk == null) &&
                (acceptsCreditCards != null ? acceptsCreditCards.equals(restaurant.acceptsCreditCards) : restaurant.acceptsCreditCards == null) &&
                (notes != null ? notes.equals(restaurant.notes) : restaurant.notes == null);
    }

    private boolean equalPhotoUrlList(List<PhotoUrl> otherPhotoUrlList) {
        if (photoUrlList == null && otherPhotoUrlList == null) return true;

        if (photoUrlList == null || otherPhotoUrlList == null) return false;

        if (photoUrlList.size() != otherPhotoUrlList.size()) return false;

        for (PhotoUrl photoUrl: otherPhotoUrlList) {
            if (! photoUrlList.contains(photoUrl) ) {
                return false;
            }
        }

        return true;
    }
}
