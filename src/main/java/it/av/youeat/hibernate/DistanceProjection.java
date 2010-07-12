package it.av.youeat.hibernate;

import it.av.youeat.ocm.model.geo.Location;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.Type;

/**
 * Projection on a distance between the restaurant and a location
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class DistanceProjection extends SimpleProjection {
    
    private Location location;

    /**
     * Constructor
     *  
     * @param location
     */
    public DistanceProjection(Location location) {
        super();
        this.location = location.toRadiant();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new Type[] { Hibernate.DOUBLE };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
        return SQLDistance.getSQLDistance(location, criteria, criteriaQuery)
        .append(" as y")
        .append(position)
        .append('_')
        .toString();
    }
}