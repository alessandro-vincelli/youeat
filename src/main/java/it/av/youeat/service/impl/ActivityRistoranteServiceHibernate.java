package it.av.youeat.service.impl;

import it.av.youeat.ocm.model.Activity;
import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.service.SocialService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Alessandro Vincelli
 * 
 */
@Repository
@Transactional(readOnly = true)
public class ActivityRistoranteServiceHibernate extends ApplicationServiceHibernate<ActivityRistorante> implements
        ActivityRistoranteService {

    @Autowired
    private EaterRelationService eaterRelationService;
    @Autowired
    private SocialService socialService;

    private String[] contributions = { ActivityRistorante.TYPE_ADDED, ActivityRistorante.TYPE_MODIFICATION,
            ActivityRistorante.TYPE_ADDED_TAG, ActivityRistorante.TYPE_NEW_COMMENT };

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
    public List<ActivityRistorante> findByEater(Eater user) {
        return findByEater(user, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByEaterFriend(Eater ofUser) {
        List<ActivityRistorante> results = new ArrayList<ActivityRistorante>();
        for (EaterRelation relation : eaterRelationService.getAllActiveRelations(ofUser)) {
            results.addAll(findByEater(relation.getToUser()));
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByEaterRistoType(Eater user, Ristorante risto, String activityType) {
        Criterion critByUser = Restrictions.eq(ActivityRistorante.USER, user);
        Criterion critByRisto = Restrictions.eq(ActivityRistorante.RISTORANTE, risto);
        Criterion critByType = Restrictions.eq(ActivityRistorante.TYPE, activityType);
        return findByCriteria(critByUser, critByType, critByRisto);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public ActivityRistorante save(ActivityRistorante activityRistorante) {
        if (activityRistorante.getEater().isSocialNetworkEater()) {
            socialService.publishRistoActivity(activityRistorante);
        }
        return super.save(activityRistorante);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByEater(Eater user, int firstResult, int maxResults) {
        Criterion crit = Restrictions.eq(ActivityRistorante.USER, user);
        Order orderByDate = Order.desc(Activity.DATE);
        return findByCriteria(orderByDate, firstResult, maxResults, crit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUserFriend(Eater ofUser, int firstResult, int maxResults) {
        return findByUserFriend(ofUser, firstResult, maxResults, false);
    }

    private List<ActivityRistorante> findByUserFriend(Eater ofUser, int firstResult, int maxResults, boolean includeTheUser) {
        // TODO, improve this method using a method that return the Friends as Eater and not as Relation
        List<EaterRelation> relations = eaterRelationService.getAllActiveRelations(ofUser);

        List<Eater> friends = new ArrayList<Eater>(relations.size());
        for (EaterRelation relation : relations) {
            friends.add(relation.getToUser());
        }
        if (includeTheUser) {
            friends.add(ofUser);
        }
        // if the user hasn't friends, just return an empty list
        else if (relations.isEmpty()) {
            return new ArrayList<ActivityRistorante>(1);
        }
        return findByEaters(friends, firstResult, maxResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByEaters(List<Eater> users, int firstResult, int maxResults) {
        return findByEaters(users, firstResult, maxResults, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFavouriteRisto(Eater user, Ristorante ristorante) {
        List<ActivityRistorante> removedAsFavourite = new ArrayList<ActivityRistorante>();
        List<ActivityRistorante> addedAsFavourite = findByEaterRistoType(user, ristorante,
                ActivityRistorante.TYPE_ADDED_AS_FAVOURITE);
        if (addedAsFavourite.size() > 0) {
            removedAsFavourite = findByEaterRistoType(user, ristorante, ActivityRistorante.TYPE_REMOVED_AS_FAVOURITE);
        }
        if (removedAsFavourite.size() > 0 && addedAsFavourite.size() > 0) {
            ActivityRistorante mostRecentAsFavourite = null;
            for (ActivityRistorante activityRistorante : addedAsFavourite) {
                if (mostRecentAsFavourite == null || activityRistorante.getDate().after(mostRecentAsFavourite.getDate())) {
                    mostRecentAsFavourite = activityRistorante;
                }
            }
            ActivityRistorante mostRecentRemovedAsFavourite = null;
            for (ActivityRistorante activityRistorante : removedAsFavourite) {
                if (mostRecentRemovedAsFavourite == null
                        || activityRistorante.getDate().after(mostRecentRemovedAsFavourite.getDate())) {
                    mostRecentRemovedAsFavourite = activityRistorante;
                }
            }
            if (mostRecentRemovedAsFavourite.getDate().after(mostRecentAsFavourite.getDate())) {
                return false;
            }
        }
        return addedAsFavourite.size() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> getLasts(int numberOfResult) {
        Order orderBy = Order.desc(ActivityRistorante.DATE);
        return findByCriteria(orderBy, 0, numberOfResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByUserFriendAndUser(Eater ofUser, int firstResult, int maxResults) {
        return findByUserFriend(ofUser, firstResult, maxResults, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByEaters(List<Eater> users, int firstResult, int maxResults, String activityType) {
        // if the users list is empty, just return an empty list
        if (users.isEmpty()) {
            return new ArrayList<ActivityRistorante>(0);
        }
        return find(users, null, firstResult, maxResults, activityType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByFriendThatEatOnRistorante(Eater eater, Ristorante risto) {
        return findByFriendWithActivitiesOnRistorante(eater, risto, ActivityRistorante.TYPE_TRIED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByFriendWithActivitiesOnRistorante(Eater eater, Ristorante risto, String activityType) {
        List<EaterRelation> friends = eaterRelationService.getFriends(eater);
        List<Eater> users = new ArrayList<Eater>(friends.size());
        for (EaterRelation eaterRelation : friends) {
            users.add(eaterRelation.getToUser());
        }
        return find(users, risto, 0, 0, activityType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityRistorante> findByFriendContributionsOnRistorante(Eater eater, Ristorante risto) {
        List<EaterRelation> friends = eaterRelationService.getFriends(eater);
        List<Eater> users = new ArrayList<Eater>(friends.size());
        for (EaterRelation eaterRelation : friends) {
            users.add(eaterRelation.getToUser());
        }
        return find(users, risto, 0, 0, contributions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countContributionsOnRistorante(Ristorante risto) {
        return countByRistoAndType(risto, contributions);
    }

    private List<ActivityRistorante> find(List<Eater> users, Ristorante risto, int firstResult, int maxResults,
            String... activityType) {
        Conjunction and = Restrictions.conjunction();
        Disjunction orUSer = Restrictions.disjunction();
        for (Eater eater : users) {
            orUSer.add(Restrictions.eq(ActivityRistorante.USER, eater));
        }
        and.add(orUSer);
        Order orderByDate = Order.desc(Activity.DATE);
        Disjunction disjunctionOnType = Restrictions.disjunction();
        for (String type : activityType) {
            if (StringUtils.isNotBlank(type)) {
                disjunctionOnType.add(Restrictions.eq(ActivityRistorante.TYPE, type));
            }
        }
        and.add(disjunctionOnType);
        if (risto != null) {
            and.add(Restrictions.eq(ActivityRistorante.RISTORANTE, risto));
        }
        return findByCriteria(orderByDate, firstResult, maxResults, and);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByRistoAndType(Ristorante risto, String... activityType) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        Conjunction senderFilter = Restrictions.conjunction();
        Criterion critByRisto = Restrictions.eq(ActivityRistorante.RISTORANTE, risto);
        Disjunction disjunction = Restrictions.disjunction();
        for (String type : activityType) {
            disjunction.add(Restrictions.eq(ActivityRistorante.TYPE, type));
        }
        senderFilter.add(disjunction);
        senderFilter.add(critByRisto);
        criteria.add(senderFilter);
        criteria.setProjection(Projections.rowCount());
        return (Integer) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void remove(ActivityRistorante object) {
        ActivityRistorante activityRistorante = getByID(object.getId());
        super.remove(activityRistorante);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeByEater(Eater eater) {
        Collection<ActivityRistorante> activities = findByEater(eater);
        for (ActivityRistorante activityRistorante : activities) {
            super.remove(activityRistorante);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> findContributedByEater(Eater eater, int maxResults) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        Criterion critByEater = Restrictions.eq(ActivityRistorante.USER, eater);
        Criterion critExcludeTried = Restrictions.ne(ActivityRistorante.TYPE, ActivityRistorante.TYPE_TRIED);
        Criterion critExcludeVoted = Restrictions.ne(ActivityRistorante.TYPE, ActivityRistorante.TYPE_VOTED);
        Criterion critExcludeAddedAsFavourite = Restrictions.ne(ActivityRistorante.TYPE,
                ActivityRistorante.TYPE_ADDED_AS_FAVOURITE);
        criteria.setProjection(Projections.distinct(Projections.property(ActivityRistorante.RISTORANTE)));
        criteria.add(critByEater);
        criteria.add(critExcludeAddedAsFavourite);
        criteria.add(critExcludeTried);
        criteria.add(critExcludeVoted);
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ristorante> findFavoriteRisto(Eater eater, int maxResults) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        Criterion critByEater = Restrictions.eq(ActivityRistorante.USER, eater);
        Criterion critIsFavourite = Restrictions.eq(ActivityRistorante.TYPE, ActivityRistorante.TYPE_ADDED_AS_FAVOURITE);
        criteria.setProjection(Projections.distinct(Projections.property(ActivityRistorante.RISTORANTE)));
        criteria.add(critByEater);
        criteria.add(critIsFavourite);
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        List<Ristorante> fullList = criteria.list();
        List<Ristorante> results = new ArrayList<Ristorante>(fullList.size());
        for (Ristorante ristorante : fullList) {
            if (isFavouriteRisto(eater, ristorante)) {
                results.add(ristorante);
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addRistoAsFavorite(Eater eater, Ristorante ristorante) {
        save(new ActivityRistorante(eater, ristorante, ActivityRistorante.TYPE_ADDED_AS_FAVOURITE));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addOrRemoveRistoAsFavorite(Eater eater, Ristorante ristorante){
        if (isFavouriteRisto(eater, ristorante)) {
            removeRistoAsFavourite(eater, ristorante);
        }
        else{
            addRistoAsFavorite(eater, ristorante);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeRistoAsFavourite(Eater eater, Ristorante ristorante) {
        save(new ActivityRistorante(eater, ristorante, ActivityRistorante.TYPE_REMOVED_AS_FAVOURITE));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void addTriedRisto(Eater eater, Ristorante ristorante) {
        save(new ActivityRistorante(eater, ristorante, ActivityRistorante.TYPE_TRIED));        
    }
}
