package it.av.youeat.service;

import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ActivityRelationService extends ApplicationService<ActivityEaterRelation> {

    /**
     * Find relation activities for the given user
     * 
     * @param eater
     * @return activities for the given user
     */
    @Transactional(readOnly = true)
    List<ActivityEaterRelation> findByEater(Eater eater);

    /**
     * Find relation activities for the given user
     * 
     * @param eater
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the given user
     */
    @Transactional(readOnly = true)
    List<ActivityEaterRelation> findByEater(Eater eater, int firstResult, int maxResults);

    /**
     * Find relation activities for the friends given user
     * 
     * @param ofUser
     * @return activities for the friends of the given user
     */
    @Transactional(readOnly = true)
    List<ActivityEaterRelation> findByEaterFriend(Eater ofUser);

    /**
     * Find relation activities for the friends given user
     * 
     * @param ofUser
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the friends of the given user
     */
    @Transactional(readOnly = true)
    List<ActivityEaterRelation> findByEaterFriend(Eater eater, int firstResult, int maxResults);

    /**
     * Find relation activities for the given users
     * 
     * @param eaters
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the given user
     */
    @Transactional(readOnly = true)
    List<ActivityEaterRelation> findByEaters(List<Eater> eaters, int firstResult, int maxResults);

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    ActivityEaterRelation save(ActivityEaterRelation activityRistorante);
}