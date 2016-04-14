package com.tokyo.beach.restaurants.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.restaurants.photos.PhotoUrl;

import java.util.List;

public class Restaurant {
    private final long id;
    private final String name;
    private String address;
    private Boolean offersEnglishMenu;
    private Boolean walkInsOk;
    private Boolean acceptsCreditCards;
    private String notes;
    private String createdDate;
    private long createdByUserId;

    public Restaurant(
            long id,
            String name,
            String address,
            Boolean offersEnglishMenu,
            Boolean walkInsOk,
            Boolean acceptsCreditCards,
            String notes,
            String createdDate,
            long createdByUserId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.offersEnglishMenu = offersEnglishMenu;
        this.walkInsOk = walkInsOk;
        this.acceptsCreditCards = acceptsCreditCards;
        this.notes = notes;
        this.createdDate = createdDate;
        this.createdByUserId = createdByUserId;
    }

    @SuppressWarnings("unused")
    static Restaurant withPhotoUrls(Restaurant restaurant, List<PhotoUrl> urls) {
        return new Restaurant(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getOffersEnglishMenu(),
                restaurant.getWalkInsOk(),
                restaurant.getAcceptsCreditCards(),
                restaurant.getNotes(),
                restaurant.getCreatedDate(),
                restaurant.getCreatedByUserId());
    }

    public long getId() {
        return id;
    }

    @JsonProperty("offers_english_menu")
    public Boolean getOffersEnglishMenu() {
        return offersEnglishMenu;
    }

    @JsonProperty("walk_ins_ok")
    public Boolean getWalkInsOk() {
        return walkInsOk;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public String getAddress() {
        return address;
    }

    @JsonProperty("accepts_credit_cards")
    public Boolean getAcceptsCreditCards() {
        return acceptsCreditCards;
    }

    @SuppressWarnings("WeakerAccess")
    public String getNotes() {
        return notes;
    }

    public long getCreatedByUserId() {
        return createdByUserId;
    }

    @JsonProperty("created_at")
    public String getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", offersEnglishMenu=" + offersEnglishMenu +
                ", walkInsOk=" + walkInsOk +
                ", acceptsCreditCards=" + acceptsCreditCards +
                ", notes='" + notes + '\'' +
                ", createdByUserId=" + createdByUserId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Restaurant that = (Restaurant) o;

        if (id != that.id) return false;
        if (createdByUserId != that.createdByUserId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (offersEnglishMenu != null ? !offersEnglishMenu.equals(that.offersEnglishMenu) : that.offersEnglishMenu != null)
            return false;
        if (walkInsOk != null ? !walkInsOk.equals(that.walkInsOk) : that.walkInsOk != null) return false;
        if (acceptsCreditCards != null ? !acceptsCreditCards.equals(that.acceptsCreditCards) : that.acceptsCreditCards != null)
            return false;
        return notes != null ? notes.equals(that.notes) : that.notes == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (offersEnglishMenu != null ? offersEnglishMenu.hashCode() : 0);
        result = 31 * result + (walkInsOk != null ? walkInsOk.hashCode() : 0);
        result = 31 * result + (acceptsCreditCards != null ? acceptsCreditCards.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (int) (createdByUserId ^ (createdByUserId >>> 32));
        return result;
    }
}
