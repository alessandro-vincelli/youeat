package it.av.youeat.service;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;

import java.util.List;

/**
 * Operation to manage activities on ristoranti
 * 
 * @author Alessandro Vincelli
 * 
 */
public interface ActivityRistoranteService {
    
    /**
     * Find activities on the given restaurant
     * 
     * @param risto
     * @return activities on the given restaurant
     */
    List<ActivityRistorante> findByRistorante(Ristorante risto);
    
    /**
     * Find by the given risto id
     * 
     * @param risto id
     * @return activities on the given restaurant id
     */
    List<ActivityRistorante> findByRistoranteId(String ristoId);

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
     * Find activities on ristoranti for the given user and activityType see {@link ActivityRistorante} for the list of type
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
     * Search restaurants with contributions by the give user
     * <p>
     * Exluding activities of type TRIED, VOTED, ADDED as FAVOURITE
     * 
     * @param eater
     * @param maxResults max nummber of result, 0 or negative for unlimited
     * @return ristos
     */
    List<Ristorante> findContributedByEater(Eater eater, int maxResults);

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
     * <i> ActivityRistorante.TYPE_ADDED, ActivityRistorante.TYPE_MODIFICATION, ActivityRistorante.TYPE_ADDED_TAG,
     * ActivityRistorante.TYPE_NEW_COMMENT </i>
     * 
     * @param eater
     * @param risto
     * @return activities on the given restaurant
     */
    List<ActivityRistorante> findByFriendContributionsOnRistorante(Eater eater, Ristorante risto);

    /**
     * Count contributions on the given ristorante<br/>
     * Activities like:<br/>
     * <i> ActivityRistorante.TYPE_ADDED, ActivityRistorante.TYPE_MODIFICATION, ActivityRistorante.TYPE_ADDED_TAG,
     * ActivityRistorante.TYPE_NEW_COMMENT </i>
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
    void remove(ActivityRistorante activity);

    /**
     * remove all the activities related to the given users
     * 
     * @param eater
     */
    void removeByEater(Eater eater);

    /**
     * Return the list of favorite restaurant of the given user
     * 
     * @param eater
     * @param maxResults max number of risto to return
     * @return
     */
    List<Ristorante> findFavoriteRisto(Eater eater, int maxResults);
    
    /**
     * Return the list of users that has the given restaurant as favorite 
     * 
     * @param risto favorites on this risto
     * @param eater friend of this user
     * @param maxResults max number of eaters to return
     * @return a list of users 
     */
    List<Eater> findEatersHasFavoritesRistoFriendsOf(Ristorante risto, Eater eater, int maxResults);
    
    /**
     * Add the given risto as favorite for the given user
     *  
     * @param eater
     * @param ristorante
     */
    void addRistoAsFavorite(Eater eater, Ristorante ristorante);
    
    /**
     * Add the given risto as favorite or remove it as favorite 
     * if it's already favorite by the given user
     *  
     * @param eater
     * @param ristorante
     */
    void addOrRemoveRistoAsFavorite(Eater eater, Ristorante ristorante);
    
    /**
     * The given user tried the given risto   
     *  
     * @param eater
     * @param ristorante
     */
    void addTriedRisto(Eater eater, Ristorante ristorante);

    /**
     * Remove the given risto as favorite for the given user
     * 
     * @param eater
     * @param ristorante
     */
    void removeRistoAsFavourite(Eater eater, Ristorante ristorante);
    
}