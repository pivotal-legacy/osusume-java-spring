package com.tokyo.beach.application.restaurant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tokyo.beach.application.photos.NewPhotoUrl;

import java.sql.Types;
import java.util.List;

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

    public NewRestaurant() {}

    public NewRestaurant(String name,
                         String address,
                         Boolean offersEnglishMenu,
                         Boolean walkInsOk,
                         Boolean acceptsCreditCards,
                         String notes,
                         List<NewPhotoUrl> photoUrls) {
        this.name = name;
        this.address = address;
        this.offersEnglishMenu = offersEnglishMenu;
        this.walkInsOk = walkInsOk;
        this.acceptsCreditCards = acceptsCreditCards;
        this.notes = notes;
        this.photoUrls = photoUrls;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Boolean getOffersEnglishMenu() {
        return offersEnglishMenu;
    }

    public Boolean getWalkInsOk() {
        return walkInsOk;
    }

    public Boolean getAcceptsCreditCards() {
        return acceptsCreditCards;
    }

    public String getNotes() {
        return notes;
    }

    public List<NewPhotoUrl> getPhotoUrls() {
        return photoUrls;
    }

    public String toString() {
        return "name: " + getName() + ",\n" +
                "address: " + getAddress() + ",\n" +
                "englishMenu: " + getOffersEnglishMenu() + ",\n" +
                "walkInsOk: " + getWalkInsOk() + ",\n" +
                "acceptsCreditCards: " + getAcceptsCreditCards() + ",\n" +
                "notes: " + getNotes();
    }

    public Object[] getParameter() {
        return new Object[]{
                getName(),
                getAddress(),
                getOffersEnglishMenu(),
                getWalkInsOk(),
                getAcceptsCreditCards(),
                getNotes()
        };
     }

    public int[] getTypes() {
        return new int[]{
                Types.VARCHAR,
                Types.VARCHAR,
                Types.BOOLEAN,
                Types.BOOLEAN,
                Types.BOOLEAN,
                Types.VARCHAR
        };
    }
}
