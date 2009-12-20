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
package it.av.eatt.service.impl;

import it.av.eatt.JackWicketException;
import it.av.eatt.JackWicketRunTimeException;
import it.av.eatt.ocm.model.ActivityRistorante;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.RateOnRistorante;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.RistoranteRevision;
import it.av.eatt.ocm.util.DateUtil;
import it.av.eatt.service.ActivityRistoranteService;
import it.av.eatt.service.RateRistoranteService;
import it.av.eatt.service.RistoranteRevisionService;
import it.av.eatt.service.RistoranteService;

import java.util.Collection;
import java.util.List;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

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
        risto.setCreationTime(DateUtil.getTimestamp());
        risto.setModificationTime(DateUtil.getTimestamp());
        risto.setRevisionNumber(1);
        risto = save(risto);
        risto.addRevision(ristoranteRevisionService.insert(new RistoranteRevision(risto)));
        risto.addActivity(activityRistoranteService.save(new ActivityRistorante(DateUtil.getTimestamp(), user, risto,
                ActivityRistorante.TYPE_ADDED)));
        return save(risto);
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
                throw new JackWicketRunTimeException("Rate non valid");
            }
        } else {
            throw new JackWicketRunTimeException("Probably the given user has already voted on the given restaurant");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUsersAlreadyRated(Ristorante risto, Eater user) {
        List<ActivityRistorante> results = activityRistoranteService.findByUserRistoType(user, risto,
                ActivityRistorante.TYPE_VOTED);
        if (results.size() == 0) {
            return false;
        } else
            return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Ristorante> find(String pattern) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, pattern);
        List<Ristorante> results = findByCriteria(critByName);
        return results;
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
        String[] fields = new String[] { "name", "city.name" };
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, fullTextEntityManager.getSearchFactory()
                .getAnalyzer("ristoranteanalyzer"));
        org.apache.lucene.search.Query query;
        try {
            query = parser.parse(pattern);
        } catch (ParseException e) {
            throw new JackWicketException(e);
        }
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Ristorante.class);
        return persistenceQuery.getResultList();
    }
}