package it.av.eatt.service.impl;

import it.av.eatt.ocm.model.Activity;
import it.av.eatt.ocm.model.ActivityRistorante;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.EaterRelation;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.service.ActivityRistoranteService;
import it.av.eatt.service.EaterRelationService;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityRistoranteServiceHibernate extends ApplicationServiceHibernate<ActivityRistorante> implements
        ActivityRistoranteService {

    @Autowired
    private EaterRelationService eaterRelationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByRistorante(Ristorante risto) {
        Criterion crit = Restrictions.eq(ActivityRistorante.RISTORANTE, risto);
        return findByCriteria(crit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUser(Eater user) {
        return findByUser(user, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUserFriend(Eater ofUser) {
        List<ActivityRistorante> results = new ArrayList<ActivityRistorante>();
        for (EaterRelation relation : eaterRelationService.getAllActiveRelations(ofUser)) {
            results.addAll(findByUser(relation.getToUser()));
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUserRistoType(Eater user, Ristorante risto, String activityType) {
        Criterion critByUser = Restrictions.eq(ActivityRistorante.USER, user);
        Criterion critByRisto = Restrictions.eq(ActivityRistorante.RISTORANTE, risto);
        Criterion critByType = Restrictions.eq(ActivityRistorante.TYPE, activityType);
        return findByCriteria(critByUser, critByType, critByRisto);
    }

    /**
     * {@inheritDoc}
     */
    public ActivityRistorante save(ActivityRistorante activityRistorante) {
        return super.save(activityRistorante);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUser(Eater user, int firstResult, int maxResults) {
        Criterion crit = Restrictions.eq(ActivityRistorante.USER, user);
        Order orderByDate = Order.desc(Activity.DATE);
        return findByCriteria(orderByDate, firstResult, maxResults, crit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUserFriend(Eater ofUser, int firstResult, int maxResults) {
        // TODO, improve this method using a method that return the Friends as Eater and not as Relation
        List<EaterRelation> relations = eaterRelationService.getAllActiveRelations(ofUser);
        // if the user hasn't friends, just return an empty list
        if (relations.isEmpty()) {
            return new ArrayList<ActivityRistorante>(0);
        }
        List<Eater> friends = new ArrayList<Eater>(relations.size());
        for (EaterRelation relation : relations) {
            friends.add(relation.getToUser());
        }
        return findByUsers(friends, firstResult, maxResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUsers(List<Eater> users, int firstResult, int maxResults) {
        // if the users list is empty, just return an empty list
        if (users.isEmpty()) {
            return new ArrayList<ActivityRistorante>(0);
        }
        Disjunction orUSer = Restrictions.disjunction();
        for (Eater eater : users) {
            orUSer.add(Restrictions.eq(ActivityRistorante.USER, eater));
        }
        Order orderByDate = Order.desc(Activity.DATE);
        return findByCriteria(orderByDate, firstResult, maxResults, orUSer);
    }
}
