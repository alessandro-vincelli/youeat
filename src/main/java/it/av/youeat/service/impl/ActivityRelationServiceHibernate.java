package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.Activity;
import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.service.ActivityRelationService;
import it.av.youeat.service.EaterRelationService;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityRelationServiceHibernate extends ApplicationServiceHibernate<ActivityEaterRelation> implements
        ActivityRelationService {

    @Autowired
    private EaterRelationService eaterRelationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityEaterRelation> findByEater(Eater eater) {
        return findByEater(eater, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityEaterRelation> findByEater(Eater eater, int firstResult, int maxResults) {
        Criterion critUser = Restrictions.eq(ActivityEaterRelation.USER, eater);
        Criterion critWithUser = Restrictions.eq(ActivityEaterRelation.WITH_USER, eater);
        Criterion or = Restrictions.disjunction().add(critUser).add(critWithUser);
        Order orderByDate = Order.desc(Activity.DATE);
        return findByCriteria(orderByDate, firstResult, maxResults, or);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityEaterRelation> findByEaterFriend(Eater ofUser) {
        return findByEaterFriend(ofUser, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityEaterRelation> findByEaterFriend(Eater eater, int firstResult, int maxResults) {
        // TODO, improve this method using a method that return the Friends as Eater and not as Relation
        List<EaterRelation> relations = eaterRelationService.getAllActiveRelations(eater);
        // if the user hasn't friends, just return an empty list
        if (relations.isEmpty()) {
            return new ArrayList<ActivityEaterRelation>(0);
        }
        List<Eater> friends = new ArrayList<Eater>(relations.size());
        for (EaterRelation relation : relations) {
            friends.add(relation.getToUser());
        }
        return findByEaters(friends, firstResult, maxResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityEaterRelation> findByEaters(List<Eater> eaters, int firstResult, int maxResults) {
        // if the users list is empty, just return an empty list
        if (eaters.isEmpty()) {
            return new ArrayList<ActivityEaterRelation>(0);
        }
        Disjunction orUSer = Restrictions.disjunction();
        for (Eater eater : eaters) {
            orUSer.add(Restrictions.eq(ActivityEaterRelation.USER, eater));
            orUSer.add(Restrictions.eq(ActivityEaterRelation.WITH_USER, eater));
        }
        Order orderByDate = Order.desc(Activity.DATE);
        return findByCriteria(orderByDate, firstResult, maxResults, orUSer);
    }
}
