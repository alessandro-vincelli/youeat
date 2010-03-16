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

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * Operations to manage relations between users
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Transactional(readOnly = true)
@Repository
public interface EaterRelationService {

    /**
     * Save a relation
     * 
     * @param relation
     * @return EaterRelation just saved
     */
    @Transactional
    EaterRelation save(EaterRelation relation);

    /**
     * Remove a relation
     * 
     * @param relation relation to remove
     */
    @Transactional
    void remove(EaterRelation relation);

    /**
     * Create a following relation
     * 
     * @param fromUser
     * @param toUser
     * @return EaterRelation, just new relation
     */
    @Transactional
    EaterRelation addFollowUser(Eater fromUser, Eater toUser);

    /**
     * Create a friend relation with {@link EaterRelation} with STATUS_PENDING
     * 
     * @param fromUser
     * @param toUser
     * @return EaterRelation, just new relation
     */
    @Transactional
    EaterRelation addFriendRequest(Eater fromUser, Eater toUser);

    /**
     * Update the relation to status STATUS_CONFIRMED
     * 
     * @param relation
     * @return EaterRelation, just updated relation
     */
    @Transactional
    EaterRelation performFriendRequestConfirm(EaterRelation relation);

    /**
     * Update the relation to status STATUS_IGNORE
     * 
     * @param relation
     * @return EaterRelation, just updated relation
     */
    @Transactional
    EaterRelation performFriendRequestIgnore(EaterRelation relation);

    /**
     * Return all the friends relations of the passed user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllFriendUsers(Eater ofUser);

    /**
     * Return all the the pending friends relations received by the given user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllPendingFriendRequetToUsers(Eater toUser);

    /**
     * Return all the follow relations of the passed user
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllFollowUsers(Eater ofUser);

    /**
     * Return all the relations of the passed user, are included also the pending relations.
     * 
     * @param ofUser
     * @return list of all friends relations
     */
    List<EaterRelation> getAllRelations(Eater ofUser);

    /**
     * Return all the active relations of the passed user
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
}
