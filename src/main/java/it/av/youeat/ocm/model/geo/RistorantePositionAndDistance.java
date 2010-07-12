package it.av.youeat.ocm.model.geo;

import static org.springframework.util.Assert.notNull;
import it.av.youeat.ocm.model.Ristorante;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class contains the info about the distance between a location and a restaurant
 * 
 * @author Alessandro Vincelli
 * 
 */
@XmlRootElement
public class RistorantePositionAndDistance {

    private RistorantePosition ristorantePosition;
    private Location location;

    /**
     * 
     * @param ristorantePosition (not null)
     * @param location (not null)
     */
    public RistorantePositionAndDistance(RistorantePosition ristorantePosition, Location location) {
        super();
        notNull(ristorantePosition);
        notNull(location);
        this.ristorantePosition = ristorantePosition;
        this.location = location;
    }

    /** 
     * @return the location of the ristorante
     */
    public RistorantePosition getRistorantePosition() {
        return ristorantePosition;
    }

    /**
     * @return the distance between the restaurant ad the location
     */
    public Long getDistanceInMeters() {
        return ristorantePosition.getWhere().distanceFrom(location);
    }

    /** 
     * @return the ristorante
     */
    public Ristorante getRistorante() {
        return ristorantePosition.getRistorante();
    }
}