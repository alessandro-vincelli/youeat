/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.service.impl;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.RateOnRistorante;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.RistoranteRevision;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.ocm.model.geo.Location;
import it.av.youeat.ocm.model.geo.RistorantePosition;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.RateRistoranteService;
import it.av.youeat.service.RistorantePositionService;
import it.av.youeat.service.RistoranteRevisionService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.util.LuceneUtil;
import it.av.youeat.util.ServerGeocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import wicket.contrib.gmap.api.GLatLng;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class RistoranteServiceHibernate extends ApplicationServiceHibernate<Ristorante> implements RistoranteService {

    @Autowired
    private ActivityRistoranteService activityRistoranteService;
    @Autowired
    private RistoranteRevisionService ristoranteRevisionService;
    @Autowired
    private RateRistoranteService rateRistoranteService;
    @Autowired
    private ServerGeocoder geocoder;
    @Autowired
    private RistorantePositionService ristorantePositionService;

    private static Log log = LogFactory.getLog(RistoranteServiceHibernate.class);

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ristorante update(Ristorante risto, Eater user) {
        Assert.notNull(user);
        risto.setModificationTime(DateUtil.getTimestamp());
        risto.setRevisionNumber(risto.getRevisionNumber() + 1);
        save(risto);
        risto.addRevision(ristoranteRevisionService.insert(new RistoranteRevision(risto)));
        activityRistoranteService.save(activityRistoranteService.save(new ActivityRistorante(DateUtil.getTimestamp(), user,
                risto, ActivityRistorante.TYPE_MODIFICATION)));

        return save(risto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ristorante updateAddress(Ristorante risto, Eater user) {
        Ristorante ristoToSave = risto;
        GLatLng gLatLng = getGLatLng(ristoToSave);
        ristoToSave.setLatitude(gLatLng.getLat());
        ristoToSave.setLongitude(gLatLng.getLng());
        if (!(gLatLng.getLat() == 0 || gLatLng.getLat() == 0)) {
            RistorantePosition position = ristorantePositionService.getByRistorante(ristoToSave);
            position.setWhere(new Location(gLatLng.getLat(), gLatLng.getLng()));
            ristorantePositionService.save(position);
        }
        return update(ristoToSave, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ristorante updateLatitudeLongitude(Ristorante risto) {
        Ristorante ristoToSave = risto;
        if (!(ristoToSave.getLatitude() == 0 || ristoToSave.getLongitude() == 0)) {
            RistorantePosition position = ristorantePositionService.getByRistorante(ristoToSave);
            if(position == null){
                position = new RistorantePosition(ristoToSave, new Location(ristoToSave.getLatitude(), ristoToSave.getLongitude()));
            }
            else{
                position.setWhere(new Location(ristoToSave.getLatitude(), ristoToSave.getLongitude()));    
            }
            ristorantePositionService.save(position);
        }
        return updateNoRevision(ristoToSave);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ristorante insert(Ristorante risto, Eater user) {
        Ristorante ristoToSave = risto;
        ristoToSave.setCreationTime(DateUtil.getTimestamp());
        ristoToSave.setModificationTime(DateUtil.getTimestamp());
        ristoToSave.setRevisionNumber(1);
        GLatLng gLatLng = getGLatLng(ristoToSave);
        ristoToSave.setLatitude(gLatLng.getLat());
        ristoToSave.setLongitude(gLatLng.getLng());
        ristoToSave = save(ristoToSave);
        ristoToSave.addRevision(ristoranteRevisionService.insert(new RistoranteRevision(ristoToSave)));
        activityRistoranteService.save(activityRistoranteService.save(new ActivityRistorante(DateUtil.getTimestamp(), user,
                ristoToSave, ActivityRistorante.TYPE_ADDED)));
        if (!(gLatLng.getLat() == 0 || gLatLng.getLat() == 0)) {
            ristorantePositionService.save(new RistorantePosition(ristoToSave, new Location(gLatLng.getLat(), gLatLng.getLng())));
        }
        return save(ristoToSave);
    }

    /**
     * Return latitude and longitude for the given risto
     * 
     * @param risto
     * @return longitude and latitude
     */
    private GLatLng getGLatLng(Ristorante risto) {
        StringBuffer address = new StringBuffer();
        address.append(risto.getAddress());
        address.append(" - ");
        address.append(risto.getPostalCode());
        address.append(" ");
        address.append(risto.getCity().getName());
        address.append(" ");
        address.append("italia");
        try {
            return geocoder.findAddress(address.toString());
        } catch (IOException e) {
            log.error("error getting address from google", e);
            return new GLatLng(0, 0);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ristorante addRate(Ristorante risto, Eater user, int rate) {
        if (user != null && risto != null && !(hasUsersAlreadyRated(risto, user))) {
            if (rate >= 0 && rate <= 5) {
                activityRistoranteService.save(new ActivityRistorante(user, risto, ActivityRistorante.TYPE_VOTED));
                risto.addARate(rateRistoranteService.insert(new RateOnRistorante(rate)));
                return save(risto);
            } else {
                throw new YoueatException("Rate non valid");
            }
        } else {
            throw new YoueatException("Probably the given user has already voted on the given restaurant");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUsersAlreadyRated(Ristorante risto, Eater user) {
        List<ActivityRistorante> results = activityRistoranteService.findByEaterRistoType(user, risto,
                ActivityRistorante.TYPE_VOTED);
        return (results.size() != 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Ristorante> find(String pattern) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, pattern);
        return findByCriteria(critByName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Ristorante updateNoRevision(Ristorante ristorante) {
        return save(ristorante);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> freeTextSearch(String pattern, ArrayList<Eater> eaters) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());
        String[] fields = new String[] { "name", "city.name", "tags.tag" };
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, fullTextEntityManager.getSearchFactory().getAnalyzer(
                "ristoranteanalyzer"));
        org.apache.lucene.search.Query query;
        try {
            String patternClean = LuceneUtil.escapeSpecialChars(pattern);
            String patternFuzzy = LuceneUtil.fuzzyAllTerms(patternClean);
            query = parser.parse(patternFuzzy);
        } catch (ParseException e) {
            throw new YoueatException(e);
        }
        FullTextQuery persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Ristorante.class);
        if (eaters != null) {
            Session session = (Session) getJpaTemplate().getEntityManager().getDelegate();
            session.enableFilter("friends").setParameterList("friendlist", eaters);
            List<Ristorante> results = persistenceQuery.getResultList();
            session.disableFilter("friends");
            return results;
        }
        return persistenceQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> freeTextSearchOnName(String pattern, City city) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());
        String[] fields = new String[] { "name" };
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, fullTextEntityManager.getSearchFactory().getAnalyzer(
                "ristoranteanalyzer"));
        org.apache.lucene.search.Query query;
        try {
            String patternClean = LuceneUtil.escapeSpecialChars(pattern);
            String patternFuzzy = LuceneUtil.fuzzyAllTerms(patternClean);
            query = parser.parse(patternFuzzy);
        } catch (ParseException e) {
            throw new YoueatException(e);
        }
        FullTextQuery persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Ristorante.class);
        if(city != null){
            Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
            criteria.add(Restrictions.eq(Ristorante.CITY, city));
            persistenceQuery.setCriteriaQuery(criteria);
        }
        return persistenceQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> freeTextSearch(String pattern) {
        return freeTextSearch(pattern, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> getLastsAdded(int numberOfResult) {
        Order orderBy = Order.desc(Ristorante.CREATION_TIME);
        return findByCriteria(orderBy, 0, numberOfResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> getLastsModified(int numberOfResult) {
        Order orderBy = Order.desc(Ristorante.MODIFICATION_TIME);
        return findByCriteria(orderBy, 0, numberOfResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> getRandom(int numberOfResult) {
        Query query = getJpaTemplate().getEntityManager().createQuery("select risto from Ristorante as risto order by random()");
        query.setMaxResults(numberOfResult);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> getByCity(City city, int firsResult, int maxResults) {
        Order orderBy = Order.desc(Ristorante.NAME);
        Criterion critByCity = Restrictions.eq(Ristorante.CITY, city);
        return findByCriteria(orderBy, firsResult, maxResults, critByCity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<City> getCityWithRisto() {
        return getCityWithRistoByCountry(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<City> getCityWithRistoByCountry(Country country) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        if (country != null) {
            criteria.add(Restrictions.eq(Ristorante.COUNTRY, country));
        }
        criteria.setProjection(Projections.distinct(Projections.property(Ristorante.CITY)));
        List<City> cities = (List<City>) criteria.list();
        Collections.sort(cities);
        return cities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Country> getCountryWithRisto() {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        criteria.setProjection(Projections.distinct(Projections.property(Ristorante.COUNTRY)));
        List<Country> countries = (List<Country>) criteria.list();
        Collections.sort(countries);
        return countries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        criteria.setProjection(Projections.rowCount());
        return (Integer) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexData() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());
        fullTextEntityManager.getSearchFactory().getAnalyzer("ristoranteanalyzer");
        Collection<Ristorante> ristos = getAll();
        int position = 0;
        for (Ristorante risto : ristos) {
            fullTextEntityManager.index(risto);
            position = position + 1;
        }
    }    
}