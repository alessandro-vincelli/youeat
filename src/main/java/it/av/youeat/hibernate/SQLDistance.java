package it.av.youeat.hibernate;

import static it.av.youeat.ocm.model.geo.RistorantePosition.COS_LATITUDE;
import static it.av.youeat.ocm.model.geo.RistorantePosition.COS_LONGITUDE;
import static it.av.youeat.ocm.model.geo.RistorantePosition.SIN_LATITUDE;
import static it.av.youeat.ocm.model.geo.RistorantePosition.SIN_LONGITUDE;
import it.av.youeat.ocm.model.geo.Location;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;

/**
 * Help class to calculate distances
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public final class SQLDistance {
    
    /**
     *  Return a SQL query to calculate the distance between the given location and a restaurant
     *  <p>Uses the following formula:
     *  <p><i>acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon1 - lon2))</i>
     *  <p><i>cos(A-B)=cos A cos B + sin A sin B
     *  
     * @param location the location to use to calculate the distance
     * @param criteria 
     * @param criteriaQuery
     * @return sql query to calculate the distance
     */
    public static final StringBuffer getSQLDistance(Location location, Criteria criteria, CriteriaQuery criteriaQuery){
        Location locationAsRadiant = location.toRadiant();
        return new StringBuffer()
        .append( locationAsRadiant.getLatitude().sin() )
        .append( " * " )
        .append( criteriaQuery.getColumn(criteria, SIN_LATITUDE) )
        .append( " + " )
        .append( locationAsRadiant.getLatitude().cos() )
        .append( " * " )
        .append( criteriaQuery.getColumn(criteria, COS_LATITUDE) )
        .append( " * (" )
        .append( locationAsRadiant.getLongitude().cos() )
        .append( " * " )
        .append( criteriaQuery.getColumn(criteria, COS_LONGITUDE) )
        .append( " + " )
        .append( locationAsRadiant.getLongitude().sin() )
        .append( " * " )
        .append( criteriaQuery.getColumn(criteria, SIN_LONGITUDE) )
        .append(")");
    }
}
