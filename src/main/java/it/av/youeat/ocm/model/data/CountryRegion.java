package it.av.youeat.ocm.model.data;

import it.av.youeat.ocm.model.BasicEntity;

import javax.persistence.Entity;

/**
 * countries and regions/state
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
public class CountryRegion extends BasicEntity{
    
    private String iso2;
    private String iso3;
    private String name;
    private String region;
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
    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }
    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }
    
}
