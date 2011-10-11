package it.av.youeat.service.impl;

import it.av.youeat.hibernate.DistanceExpression;
import it.av.youeat.hibernate.DistanceOrder;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.geo.Location;
import it.av.youeat.ocm.model.geo.RistorantePosition;
import it.av.youeat.ocm.model.geo.RistorantePositionAndDistance;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.RistorantePositionService;
import it.av.youeat.service.RistoranteService;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Davide Cerbo
 * @author Alessandro Vincelli
 * 
 */
@Transactional(readOnly = true)
public class RistorantePositionServiceHibernate extends ApplicationServiceHibernate<RistorantePosition> implements
        RistorantePositionService {
    
    @Autowired
    private ActivityRistoranteService activityRistoranteService;
    @Autowired
    private RistoranteService ristoranteService;

    /**
     * {@inheritDoc}
     */
    //@Override
//    public List<RistorantePositionAndDistance> around(Location location, long meters, int maxResults) {
//        // acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon1 - lon2));
//        // cos(A-B)=cos A cos B + sin A sin B
//        Location locationAsRadiant = location.toRadiant();
//        // using this calculation I have saving two calculation during the query: the cos and the multiplication
//        double confrotableMeters = Math.cos(meters / GeoUtil.RADIUS);
//        String selectDistanceInMeters = "(?1 * w.sinLatitude + ?2 * w.cosLatitude * "
//                + "(?3 * w.cosLongitude + ?4 * w.sinLongitude)) as metri";
//        // Projection metri = Projections.sqlProjection(selectDistanceInMeters, {"metri"}, {FloatType.class]);
//        String distanceInMeters = "(?5 * w.sinLatitude + ?6 * w.cosLatitude * " + "(?7 * w.cosLongitude + ?8 * w.sinLongitude))";
//        String orderInMeters = "(?9 * w.sinLatitude + ?10 * w.cosLatitude * " + "(?11 * w.cosLongitude + ?12 * w.sinLongitude))";
//
//        String hql = "select new it.av.youeat.ocm.model.geo.RistorantePositionAndDistance(w, " + selectDistanceInMeters
//                + ")  from RistorantePosition w where " + distanceInMeters + " >= ?13 order by " + orderInMeters + " desc";
//        Query criteria = getJpaTemplate().getEntityManager().createQuery(hql);
//
//        // return getJpaTemplate().find("select new it.av.youeat.ocm.model.geo.RistorantePositionAndDistance(w, " +
//        // selectDistanceInMeters + ")  from RistorantePosition w where " + distanceInMeters + " >= ?13 order by " +
//        // orderInMeters+ " desc limit ?14",
//        criteria.setParameter(1, locationAsRadiant.latitude.sin());
//        criteria.setParameter(2, locationAsRadiant.latitude.cos());
//        criteria.setParameter(3, locationAsRadiant.longitude.cos());
//        criteria.setParameter(4, locationAsRadiant.longitude.sin());
//        criteria.setParameter(5, locationAsRadiant.latitude.sin());
//        criteria.setParameter(6, locationAsRadiant.latitude.cos());
//        criteria.setParameter(7, locationAsRadiant.longitude.cos());
//        criteria.setParameter(8, locationAsRadiant.longitude.sin());
//        criteria.setParameter(9, locationAsRadiant.latitude.sin());
//        criteria.setParameter(10, locationAsRadiant.latitude.cos());
//        criteria.setParameter(11, locationAsRadiant.longitude.cos());
//        criteria.setParameter(12, locationAsRadiant.longitude.sin());
//        criteria.setParameter(13, confrotableMeters);
//        criteria.setMaxResults(maxResults);
//        return criteria.getResultList();
//    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<RistorantePositionAndDistance> around(Location location, long meters, int firstResults, int maxResults) {
        return findByParams(location, firstResults, maxResults, new DistanceExpression(location, ">=", meters));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<RistorantePositionAndDistance> favourites(Eater eater, Location location,int firstResult, int maxResults) {
        List<Ristorante> ristos = activityRistoranteService.findFavoriteRisto(eater, maxResults);
        Criterion critByFavoriteRistos = Restrictions.in(RistorantePosition.RISTORANTE_FIELD, ristos);
        return findByParams(location, firstResult, maxResults, critByFavoriteRistos);
    }

    /**
     * base find methods  
     * 
     * @param location
     * @param firstResult the first result to retrieve, numbered from <tt>0</tt>
     * @param maxResults
     * @param criterions
     * @return
     */
    private List<RistorantePositionAndDistance> findByParams(Location location, int firstResult, int maxResults, Criterion...criterions) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        for (Criterion criterion : criterions) {
            criteria.add(criterion);    
        }
        criteria.addOrder(DistanceOrder.desc(location));
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);
        List<RistorantePosition> results = criteria.list();
        List<RistorantePositionAndDistance> positionAndDistances = new ArrayList<RistorantePositionAndDistance>(results.size());
        for (RistorantePosition ristorantePosition : results) {
            positionAndDistances.add(new RistorantePositionAndDistance(ristorantePosition, location));            
        }
        return positionAndDistances;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public RistorantePosition getByRistorante(Ristorante ristorante) {
        Criterion criteria = Restrictions.eq(RistorantePosition.RISTORANTE_FIELD, ristorante);
        List<RistorantePosition> result = findByCriteria(criteria);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RistorantePositionAndDistance> aroundFreeTextSearch(String pattern, Location location, long meters,
            int firstResult, int maxResults) {
        //free text search on restaurants
        List<Ristorante> r = ristoranteService.freeTextSearch(pattern);
        List<String> ids = new ArrayList<String>(r.size()); 
        for (Ristorante ristorante : r) {
            ids.add(ristorante.getId());
        }
        // get position for restaurants
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.in("ristorante.id", ids));
        criteria.addOrder(DistanceOrder.desc(location));
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);
        List<RistorantePosition> rp = criteria.list();
        // calculates distances for restaurants
        List<RistorantePositionAndDistance> rpd = new ArrayList<RistorantePositionAndDistance>(maxResults);
        for (int i = firstResult; ( i < maxResults && i <= rpd.size() ); i++) {
            if(rp.get(i) != null){
                rpd.add(new RistorantePositionAndDistance(rp.get(i), location));    
            }
        }
        for (RistorantePosition ristorantePosition : rp) {
            rpd.add(new RistorantePositionAndDistance(ristorantePosition, location));            
        }
        return rpd;
    }

}