package com.tokyo.beach.application.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.application.photos.NewPhotoUrl;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class NewRestaurant {

    private String name;
    private String address;

    @JsonProperty("offers_english_menu")
    private Boolean offersEnglishMenu;

    @JsonProperty("walk_ins_ok")
    private Boolean walkInsOk;

    @JsonProperty("accepts_credit_cards")
    private Boolean acceptsCreditCards;

    private String notes;

    @JsonProperty("photo_urls")
    private List<NewPhotoUrl> photoUrls;

    @JsonProperty("cuisine_id")
    private Long cuisineId;

    public NewRestaurant() {
    }

    public NewRestaurant(String name,
                         String address,
                         Boolean offersEnglishMenu,
                         Boolean walkInsOk,
                         Boolean acceptsCreditCards,
                         String notes,
                         Long cuisineId,
                         List<NewPhotoUrl> photoUrls) {
        this.name = name;
        this.address = address;
        this.offersEnglishMenu = offersEnglishMenu;
        this.walkInsOk = walkInsOk;
        this.acceptsCreditCards = acceptsCreditCards;
        this.notes = notes;
        this.cuisineId = cuisineId;
        this.photoUrls = photoUrls;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public String getAddress() {
        return address;
    }

    @SuppressWarnings("WeakerAccess")
    public Boolean getOffersEnglishMenu() {
        return offersEnglishMenu;
    }

    @SuppressWarnings("WeakerAccess")
    public Boolean getWalkInsOk() {
        return walkInsOk;
    }

    @SuppressWarnings("WeakerAccess")
    public Boolean getAcceptsCreditCards() {
        return acceptsCreditCards;
    }

    @SuppressWarnings("WeakerAccess")
    public String getNotes() {
        return notes;
    }

    @SuppressWarnings("WeakerAccess")
    public List<NewPhotoUrl> getPhotoUrls() {
        return photoUrls;
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<Long> getCuisineId() {
        return Optional.ofNullable(cuisineId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewRestaurant that = (NewRestaurant) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (offersEnglishMenu != null ? !offersEnglishMenu.equals(that.offersEnglishMenu) : that.offersEnglishMenu != null)
            return false;
        if (walkInsOk != null ? !walkInsOk.equals(that.walkInsOk) : that.walkInsOk != null) return false;
        if (acceptsCreditCards != null ? !acceptsCreditCards.equals(that.acceptsCreditCards) : that.acceptsCreditCards != null)
            return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (photoUrls != null ? !photoUrls.equals(that.photoUrls) : that.photoUrls != null) return false;
        return cuisineId != null ? cuisineId.equals(that.cuisineId) : that.cuisineId == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (offersEnglishMenu != null ? offersEnglishMenu.hashCode() : 0);
        result = 31 * result + (walkInsOk != null ? walkInsOk.hashCode() : 0);
        result = 31 * result + (acceptsCreditCards != null ? acceptsCreditCards.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (photoUrls != null ? photoUrls.hashCode() : 0);
        result = 31 * result + (cuisineId != null ? cuisineId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NewRestaurant{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", offersEnglishMenu=" + offersEnglishMenu +
                ", walkInsOk=" + walkInsOk +
                ", acceptsCreditCards=" + acceptsCreditCards +
                ", notes='" + notes + '\'' +
                ", photoUrls=" + photoUrls +
                ", cuisineId=" + cuisineId +
                '}';
    }
}
