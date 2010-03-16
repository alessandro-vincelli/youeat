package it.av.youeat.service;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operation to manage activities on ristoranti 
 * 
 * @author Alessandro Vincelli
 *
 */
@Transactional(readOnly = true)
@Repository
public interface ActivityRistoranteService {
    /**
     * Find activities on the given restaurant
     * 
     * @param risto
     * @return activities on the given restaurant
     */
    List<ActivityRistorante> findByRistorante(Ristorante risto);

    /**
     * Find activities on ristoranti for the given user
     * 
     * @param user
     * @return activities for the given user
     */
    List<ActivityRistorante> findByEater(Eater user);

    /**
     * Find activities on ristoranti for the given user
     * 
     * @param user
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the given user
     */
    List<ActivityRistorante> findByEater(Eater user, int firstResult, int maxResults);

    /**
     * Find activities on ristoranti for the given user and activityType see {@link ActivityRistorante} for the list of
     * type
     * 
     * @param user
     * @param risto
     * @param activityType
     * @return activities for the given user
     */
    List<ActivityRistorante> findByEaterRistoType(Eater user, Ristorante risto, String activityType);
    
    /**
     * Count activities on ristoranti of the activityType see {@link ActivityRistorante}
     * 
     * @param risto
     * @param activityType
     * @return number of activities
     */
    int countByRistoAndType(Ristorante risto, String... activityType);

    /**
     * Find activities on restaurants done by friends of given user
     * 
     * @param ofUser
     * @return activities for the friends of the given user
     */
    List<ActivityRistorante> findByEaterFriend(Eater ofUser);

    /**
     * Find activities on restaurants for the friends given user
     * 
     * @param ofUser
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the friends of the given user
     */
    List<ActivityRistorante> findByUserFriend(Eater ofUser, int firstResult, int maxResults);
    
    /**
     * Find activities on restaurants for the friends of the given user and of the user himself 
     * 
     * @param ofUser
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the friends of the given user
     */
    List<ActivityRistorante> findByUserFriendAndUser(Eater ofUser, int firstResult, int maxResults);

    /**
     * Find activities on ristoranti for the given users
     * 
     * @param users
     * @param firstResult first result
     * @param maxResults number of results
     * @return activities for the given user
     */
    List<ActivityRistorante> findByEaters(List<Eater> users, int firstResult, int maxResults);
    
    /**
     * Find activities on ristoranti for the given users
     * 
     * @param users
     * @param firstResult first result
     * @param maxResults number of results
     * @param activityType activity type, null to disable 
     * @return activities for the given user
     */
    List<ActivityRistorante> findByEaters(List<Eater> users, int firstResult, int maxResults, String activityType);

    /**
     * Is the given ristorante favourite for the given user
     * 
     * @param user
     * @param ristorante
     * @return true if the risto is favourite
     */
    boolean isFavouriteRisto(Eater user, Ristorante ristorante);

    /**
     * {@inheritDoc}
     */
    @Transactional
    ActivityRistorante save(ActivityRistorante activityRistorante);

    /**
     * Return the last activities on risto
     * 
     * @param numberOfResult number of result to return
     * @return lasts activities
     */
    List<ActivityRistorante> getLasts(int numberOfResult);

    /**
     * Find friends of the given eater that eat on the given ristorante
     * 
     * @param eater 
     * @param risto
     * @return activities on the given restaurant
     */
    List<ActivityRistorante> findByFriendThatEatOnRistorante(Eater eater, Ristorante risto);

    /**
     * Find friends that tried the given ristorante
     * 
     * @param eater 
     * @param risto
     * @param activityType null to disable
     * @return activities on the given restaurant
     */
    List<ActivityRistorante> findByFriendWithActivitiesOnRistorante(Eater eater, Ristorante risto, String activityType);

    /**
     * Find friends activities on the given ristorante<br/>
     * Activities like:<br/>
     * <i>
     * ActivityRistorante.TYPE_ADDED, ActivityRistorante.TYPE_MODIFICATION, ActivityRistorante.TYPE_ADDED_TAG, ActivityRistorante.TYPE_NEW_COMMENT
     * </i>
     * 
     * @param eater 
     * @param risto
     * @return activities on the given restaurant
     */
    List<ActivityRistorante> findByFriendContributionsOnRistorante(Eater eater, Ristorante risto);

    /**
     * Count contributions on the given ristorante<br/>
     * Activities like:<br/>
     * <i>
     * ActivityRistorante.TYPE_ADDED, ActivityRistorante.TYPE_MODIFICATION, ActivityRistorante.TYPE_ADDED_TAG, ActivityRistorante.TYPE_NEW_COMMENT
     * </i>
     * 
     * @param eater 
     * @param risto
     * @return activities on the given restaurant
     */
    int countContributionsOnRistorante(Ristorante risto);

    /**
     * Remove the given activity
     * 
     * @param activity activity to remove
     */
    @Transactional
    void remove(ActivityRistorante activity);
    
    /**
     * remove all the activities related to the given users
     * 
     * @param eater
     */
    @Transactional
    void removeByEater(Eater eater);
}