package com.tokyo.beach;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Types;

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

    public NewRestaurant() {}

    public NewRestaurant(String name,
                         String address,
                         Boolean offersEnglishMenu,
                         Boolean walkInsOk,
                         Boolean acceptsCreditCards,
                         String notes) {
        this.name = name;
        this.address = address;
        this.offersEnglishMenu = offersEnglishMenu;
        this.walkInsOk = walkInsOk;
        this.acceptsCreditCards = acceptsCreditCards;
        this.notes = notes;
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

    public String toString() {
        return "name: " + getName() + ",\n" +
                "address: " + getAddress() + ",\n" +
                "englishMenu: " + getOffersEnglishMenu() + ",\n" +
                "walkInsOk: " + getWalkInsOk() + ",\n" +
                "acceptsCreditCards: " + getAcceptsCreditCards() + ",\n" +
                "notes: " + getNotes();
    }

    public Object[] getParameter() {
        Object[] params = {
                getName(),
                getAddress(),
                getOffersEnglishMenu(),
                getWalkInsOk(),
                getAcceptsCreditCards(),
                getNotes()
        };
        return params;
    }

    public int[] getTypes() {
        int[] types =  {
                Types.VARCHAR,
                Types.VARCHAR,
                Types.BOOLEAN,
                Types.BOOLEAN,
                Types.BOOLEAN,
                Types.VARCHAR
        };
        return types;
    }
}
