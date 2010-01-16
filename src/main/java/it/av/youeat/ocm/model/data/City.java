package it.av.youeat.ocm.model.data;

import it.av.youeat.ocm.model.BasicEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed
public class City extends BasicEntity {

    public final static String NAME_FIELD = "name";

    public static final String COUNTRY_FIELD = "country";

    @ManyToOne(optional=false)
    @ForeignKey(name="city_to_country_fk")
    private Country country;
    private String nameSimplified;
    @Field(index=Index.TOKENIZED, store=Store.NO)
    @org.hibernate.annotations.Index(name="city_name_idx")
    private String name;
    private String region;
    private String latitude;
    private String longitude;

    
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getNameSimplified() {
        return nameSimplified;
    }

    public void setNameSimplified(String nameSimplified) {
        this.nameSimplified = nameSimplified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return getName();
    }
}