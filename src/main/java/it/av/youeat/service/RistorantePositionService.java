package it.av.youeat.service;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.geo.Location;
import it.av.youeat.ocm.model.geo.RistorantePosition;
import it.av.youeat.ocm.model.geo.RistorantePositionAndDistance;

import java.util.List;

/**
 * 
 * @author Davide Cerbo
 * @author Alessandro Vincelli
 * 
 */
public interface RistorantePositionService {

    /**
     * Search restaurants around a position.<br>
     * Returns a list of RistorantePositionAndDistance object containing info about found found ristos and relative distance.  
     * 
     * @param location the location
     * @param meters the max distance in meter
     * @param maxResults max number of result to return
     * @return a list of RistorantePositionAndDistance
     */
    List<RistorantePositionAndDistance> around(Location location, long meters, int maxResults);

    /**
     * Saves the given position
     * 
     * @param position
     * @return just created Tag or already present tag
     */
    RistorantePosition save(RistorantePosition position);

    /**
     * Returns all the position
     * 
     * @return all the position
     */
    List<RistorantePosition> getAll();

    /**
     * Removes the given position
     * 
     * @param position the item to remove
     */
    void remove(RistorantePosition position);

    /**
     * Return the position of the given ristorante
     * 
     * @param ristorante
     */
    RistorantePosition getByRistorante(Ristorante ristorante);
}
