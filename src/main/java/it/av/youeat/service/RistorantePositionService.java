package it.av.youeat.service;

import it.av.youeat.ocm.model.Eater;
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
     * Search restaurants around a position.
     * <p>Returns a list of RistorantePositionAndDistance object containing info about found found ristos and relative distance.  
     * 
     * @param location the location
     * @param meters the max distance in meter
     * @param firstResult the first result to retrieve, numbered from <tt>0</tt>
     * @param maxResults max number of result to return
     * @return a list of RistorantePositionAndDistance
     */
    List<RistorantePositionAndDistance> around(Location location, long meters, int firstResult, int maxResults);
    
    /**
     * Favorites restaurants of a user, plus the distance between the risto and the position.
     * 
     * @param eater the user 
     * @param location the location to calculate the distance
     * @param maxResults max number of result to return
     * @param firstResult the first result to retrieve, numbered from <tt>0</tt>
     * @return a list of RistorantePositionAndDistance
     */
    List<RistorantePositionAndDistance> favourites(Eater eater, Location location, int firstResult, int maxResults);

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
