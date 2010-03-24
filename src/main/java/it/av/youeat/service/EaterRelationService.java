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
package it.av.youeat.service;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;

import java.util.List;

/**
 * 
 * Operations to manage relations between users
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface EaterRelationService {

    /**
     * Save a relation
     * 
     * @param relation
     * @return just saved relation 
     */
    EaterRelation save(EaterRelation relation);

    /**
     * Remove a relation
     * 
     * @param relation relation to remove
     */
    void remove(EaterRelation relation);

    /**
     * Create a following relation
     * 
     * @param fromUser
     * @param toUser
     * @return new relation
     */
    EaterRelation addFollowUser(Eater fromUser, Eater toUser);

    /**
     * Create a friend relation with {@link EaterRelation} with STATUS_PENDING
     * 
     * @param fromUser
     * @param toUser
     * @return new relation
     */
    EaterRelation addFriendRequest(Eater fromUser, Eater toUser);

    /**
     * Update the relation to status STATUS_CONFIRMED
     * 
     * @param relation
     * @return updated relation
     */
    EaterRelation performFriendRequestConfirm(EaterRelation relation);

    /**
     * Update the relation to status STATUS_IGNORE
     * 
     * @param relation
     * @return updated relation
     */
    EaterRelation performFriendRequestIgnore(EaterRelation relation);

    /**
     * Return all active friend relations of the given user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getFriends(Eater ofUser);

    /**
     * Return all the the pending friends relations received by the given user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllPendingFriendRequetToUsers(Eater toUser);

    /**
     * Return all the follow relations of the given user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllFollowUsers(Eater ofUser);

    /**
     * Return all the relations of the given user, are included also the pending relations.
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllRelations(Eater ofUser);

    /**
     * Return all the active relations for the givem user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllActiveRelations(Eater ofUser);

    /**
     * Returns the relation between two users
     * 
     * @param ofUser
     * @param toUser
     * @return relation between two user, <b>null</b> if no relation
     */
    EaterRelation getRelation(Eater ofUser, Eater toUser);

    /**
     * Remove all the relation of the given eater
     * 
     * @param eater
     */
    void removeByEater(Eater eater);

    /**
     * Gets the list of common friend between two users
     * 
     * @param eaterA
     * @param eaterB
     * @return a set of common friends
     */
    List<Eater> getCommonFriends(Eater eaterA, Eater eaterB);
    
    /**
     * Gets the list of user friend of eaterA but not friend of eaterB
     * 
     * @param eaterA
     * @param eaterB
     * @return a set of non common friends
     */
    List<Eater> getNonCommonFriends(Eater eaterA, Eater eaterB);
    
    /**
     * Count commons friend between two users
     * 
     * @param eaterA
     * @param eaterB
     * @return number of common friend
     */
    int countCommonFriends(Eater eaterA, Eater eaterB);
    
    /**
     * Count active friends for the given user
     * 
     * @param eater
     * @return a set of common eater
     */
    int countFriends(Eater eater);
}
