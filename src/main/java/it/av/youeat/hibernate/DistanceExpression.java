package it.av.youeat.hibernate;

import it.av.youeat.ocm.model.geo.Location;
import it.av.youeat.util.GeoUtil;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

/**
 * A query to filter restaurants using the distance from a location 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class DistanceExpression implements Criterion {

    private static final TypedValue[] NO_VALUES = new TypedValue[0];
    
    private final Location location;
    private final String op;
    private final long meters;
    
    /**
     * Constructor of an expression to filter restaurants using the given distance on the given location   
     * 
     * @param location
     * @param op operators, could be =, >=, <=, !=
     * @param meters the distance to use as a filter
     */
    public DistanceExpression(Location location, String op, long meters) {
        super();
        this.location = location;
        this.op = op;
        this.meters = meters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return NO_VALUES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return SQLDistance.getSQLDistance(location, criteria, criteriaQuery)
        .append(op)
        .append(" ")
        .append(Math.cos(meters / GeoUtil.RADIUS))
        .toString();
    }
}
