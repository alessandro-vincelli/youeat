package it.av.youeat.hibernate;

import it.av.youeat.ocm.model.geo.Location;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

/**
 * Represents an order imposed upon a <tt>Ristorante</tt> result set using the the distance
 * from a location 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class DistanceOrder extends Order {

    private final Location location;
    private final boolean ascending;

    /**
     * Sorts the restaurant using the distance from the given location
     * 
     * @param location
     * @param ascending
     */
    protected DistanceOrder(Location location, boolean ascending) {
        super("distance", true);
        this.ascending = ascending;
        this.location = location;
    }

    /**
     * Render the SQL fragment
     * 
     */
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        StringBuffer fragment = SQLDistance.getSQLDistance(location, criteria, criteriaQuery);
        fragment.append(ascending ? " asc" : " desc");
        return fragment.toString();
    }

    /**
     * Ascending order for restaurant using the distance from the given location
     * 
     * @param location
     * @return DistanceOrder
     */
    public static DistanceOrder asc(Location location) {
        return new DistanceOrder(location, true);
    }

    /**
     * Descending order for restaurant using the distance from the given location
     * 
     * @param location
     * @return DistanceOrder
     */
    public static DistanceOrder desc(Location location) {
        return new DistanceOrder(location, false);
    }

}
