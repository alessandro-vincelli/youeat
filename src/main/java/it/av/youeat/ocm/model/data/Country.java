package it.av.youeat.ocm.model.data;

import it.av.youeat.ocm.model.BasicEntity;

import javax.persistence.Entity;

import org.hibernate.annotations.Index;

/**
 * countries
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
public class Country extends BasicEntity{
    
    public static final String NAME = "name";
    
    @Index(name="iso2name")
    private String iso2;
    private String iso3;
    @Index(name="countryname")
    private String name;
    
    
    /**
     * @param iso2
     * @param iso3
     * @param name
     */
    public Country(String iso2, String iso3, String name) {
        super();
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.name = name;
    }
    /**
     * default constructor 
     */
    public Country() {
        super();
    }
    /**
     * @return the iso2
     */
    public String getIso2() {
        return iso2;
    }
    /**
     * @param iso2 the iso2 to set
     */
    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }
    /**
     * @return the iso3
     */
    public String getIso3() {
        return iso3;
    }
    /**
     * @param iso3 the iso3 to set
     */
    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}