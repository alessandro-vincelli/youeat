package it.av.youeat.ocm.model;

import javax.persistence.Entity;

@Entity
public class Language extends BasicEntity {

    private String language;
    private String country;

    public Language() {
        super();
    }

    public Language(String language, String country) {
        super();
        this.language = language;
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return language;
    }

}