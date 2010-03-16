package it.av.youeat.service;

import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operation to manage activities on user relations
 * 
 * @author Alessandro Vincelli
 * 
 */
@Transactional(readOnly = true)
@Repository
public interface ActivityRelationService {

    /**
     * Find relation activities for the given user
     * 
     * @param eater
     * @return activities for the given user
     */
    List<ActivityEaterRelation> findByEater(Eater eater);

    /**
     * Find relation activities for the given user
     * 
     * @param eater
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the given user
     */
    List<ActivityEaterRelation> findByEater(Eater eater, int firstResult, int maxResults);

    /**
     * Find relation activities for the friends given user
     * 
     * @param ofUser
     * @return activities for the friends of the given user
     */
    List<ActivityEaterRelation> findByEaterFriend(Eater ofUser);

    /**
     * Find relation activities for the friends given user
     * 
     * @param ofUser
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the friends of the given user
     */
    List<ActivityEaterRelation> findByEaterFriend(Eater eater, int firstResult, int maxResults);

    /**
     * Find relation activities for the friends given user and for the user himself
     * 
     * @param ofUser
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the friends of the given user
     */
    List<ActivityEaterRelation> findByEaterFriendAndEater(Eater eater, int firstResult, int maxResults);

    /**
     * Find relation activities for the given users
     * 
     * @param eaters
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the given user
     */
    List<ActivityEaterRelation> findByEaters(List<Eater> eaters, int firstResult, int maxResults);

    /**
     * store the activity in the database
     * 
     * @param activityRistorante the activity to save
     * @return the just saved activity
     */
    @Transactional
    ActivityEaterRelation save(ActivityEaterRelation activityRistorante);

    /**
     * remove all the activities related to the given users
     * 
     * @param eater
     */
    @Transactional
    void removeByEater(Eater eater);

}