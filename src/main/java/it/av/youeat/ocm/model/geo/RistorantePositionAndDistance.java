package it.av.youeat.ocm.model.geo;

import static it.av.youeat.util.GeoUtil.toMeter;
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
    private Double distance;

    /**
     * 
     * @param ristorantePosition (not null)
     * @param location (not null)
     */
    public RistorantePositionAndDistance(RistorantePosition ristorantePosition, Double distance) {
        super();
        notNull(ristorantePosition);
        notNull(distance);
        this.ristorantePosition = ristorantePosition;
        this.distance = distance;
    }

    public RistorantePosition getRistorantePosition() {
        return ristorantePosition;
    }

    public Double getDistance() {
        return distance;
    }

    public Long getDistanceInMeters() {
        return toMeter(distance);
    }

    public Ristorante getRistorante() {
        return ristorantePosition.getRistorante();
    }

}
