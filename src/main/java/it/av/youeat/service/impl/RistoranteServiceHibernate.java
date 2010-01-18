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
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.RateRistoranteService;
import it.av.youeat.service.RistoranteRevisionService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.util.LuceneUtil;
import it.av.youeat.util.ServerGeocoder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import wicket.contrib.gmap.api.GLatLng;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class RistoranteServiceHibernate extends ApplicationServiceHibernate<Ristorante> implements RistoranteService {

    @Autowired
    private ActivityRistoranteService activityRistoranteService;
    @Autowired
    private RistoranteRevisionService ristoranteRevisionService;
    @Autowired
    private RateRistoranteService rateRistoranteService;
    @Autowired
    private ServerGeocoder geocoder;
    private static Log log = LogFactory.getLog(RistoranteServiceHibernate.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Ristorante update(Ristorante risto, Eater user) {
        risto.setModificationTime(DateUtil.getTimestamp());
        risto.setRevisionNumber(risto.getRevisionNumber() + 1);
        save(risto);
        risto.addRevision(ristoranteRevisionService.insert(new RistoranteRevision(risto)));
        risto.addActivity(activityRistoranteService.save(new ActivityRistorante(DateUtil.getTimestamp(), user, risto,
                ActivityRistorante.TYPE_MODIFICATION)));
        return save(risto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
        ristoToSave.addActivity(activityRistoranteService.save(new ActivityRistorante(DateUtil.getTimestamp(), user,
                ristoToSave, ActivityRistorante.TYPE_ADDED)));
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
        address.append(" ");
        address.append(risto.getPostalCode());
        address.append(" ");
        address.append(risto.getCity().getName());
        address.append(" ");
        address.append(risto.getCountry().getName());
        try {
            return geocoder.findAddress(address.toString());
        } 
        catch (IOException e) {
            log.error("error getting address from google", e);
            return new GLatLng(0, 0);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ristorante addRate(Ristorante risto, Eater user, int rate) {
        if (user != null && risto != null && !(hasUsersAlreadyRated(risto, user))) {
            if (rate >= 0 && rate <= 5) {
                ActivityRistorante activity = activityRistoranteService.save(new ActivityRistorante(user, risto,
                        ActivityRistorante.TYPE_VOTED));
                risto.addARate(rateRistoranteService.insert(new RateOnRistorante(rate, activity)));
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
        List<ActivityRistorante> results = activityRistoranteService.findByUserRistoType(user, risto,
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
    public void remove(Ristorante object) {
        // for (ActivityRistorante activityRistorante : object.getActivities()) {
        // getJpaTemplate().remove(activityRistorante);
        // }
        // for (TagOnRistorante tag : object.getTags()) {
        // getJpaTemplate().remove(tag);
        // }
        // for (RateOnRistorante rate : object.getRates()) {
        // getJpaTemplate().remove(rate);
        // }
        // for(RistoranteRevision revision :object.getRevisions()){
        // getJpaTemplate().remove(revision);
        // }
        // getJpaTemplate().flush();
        super.remove(object);
    }

    /**
     * @param activityRistoranteService
     */
    public void setActivityRistoranteService(ActivityRistoranteService activityRistoranteService) {
        this.activityRistoranteService = activityRistoranteService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ristorante updateNoRevision(Ristorante ristorante) {
        return save(ristorante);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> freeTextSearch(String pattern) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getJpaTemplate().getEntityManager());
        String[] fields = new String[] { "name", "city.name", "tags.tag" };
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, fullTextEntityManager.getSearchFactory()
                .getAnalyzer("ristoranteanalyzer"));
        org.apache.lucene.search.Query query;
        try {
            query = parser.parse(LuceneUtil.escapeSpecialChars(pattern));
        } catch (ParseException e) {
            throw new YoueatException(e);
        }
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Ristorante.class);
        return persistenceQuery.getResultList();
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
        Query query = getJpaTemplate().getEntityManager().createQuery(
                "select risto from Ristorante as risto order by random()");
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
}