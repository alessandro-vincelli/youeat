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
package it.av.youeat.service.impl;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.ActivityRelationService;
import it.av.youeat.service.EaterRelationService;

import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class EaterRelationServiceHibernate extends ApplicationServiceHibernate<EaterRelation> implements
        EaterRelationService {

    @Autowired
    private ActivityRelationService activityRelationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation addFollowUser(Eater fromUser, Eater toUser) {
        EaterRelation relation = EaterRelation.createFollowRelation(fromUser, toUser);
        relation = save(relation);
        ActivityEaterRelation activity = new ActivityEaterRelation(DateUtil.getTimestamp(), fromUser, toUser,
                ActivityEaterRelation.TYPE_STARTS_FOLLOWING);
        activityRelationService.save(activity);
        return relation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation addFriendRequest(Eater fromUser, Eater toUser) {
        EaterRelation relation = EaterRelation.createFriendRelation(fromUser, toUser);
        // it's necessary check if we are trying to recreate a relation previously removed
        // and that is still present in the other direction
        List<EaterRelation> oppositeRelations = getOppositeRelation(relation);
        for (EaterRelation er : oppositeRelations) {
            // if exist an opposite relation with status Active or pending we recreate the same relation
            if (er.getType().equals(EaterRelation.TYPE_FRIEND) && !er.getStatus().equals(EaterRelation.STATUS_IGNORED)) {
                relation.setStatus(er.getStatus());
            }
        }
        return save(relation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation performFriendRequestConfirm(EaterRelation relation) {
        if (relation != null && relation.getStatus().equals(EaterRelation.STATUS_PENDING)
                && relation.getType().equals(EaterRelation.TYPE_FRIEND)) {
            relation.setStatus(EaterRelation.STATUS_ACTIVE);
            EaterRelation inverseRelation = new EaterRelation();
            inverseRelation.setFromUser(relation.getToUser());
            inverseRelation.setToUser(relation.getFromUser());
            inverseRelation.setStartDate(DateUtil.getTimestamp());
            inverseRelation.setType(EaterRelation.TYPE_FRIEND);
            inverseRelation.setStatus(EaterRelation.STATUS_ACTIVE);
            save(inverseRelation);
            EaterRelation savedRelation = save(relation);
            ActivityEaterRelation activity = new ActivityEaterRelation(DateUtil.getTimestamp(), savedRelation
                    .getFromUser(), savedRelation.getToUser(), ActivityEaterRelation.TYPE_ARE_FRIENDS);
            activityRelationService.save(activity);
            return savedRelation;
        } else {
            throw new YoueatException("Relation cannot be updated");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation performFriendRequestIgnore(EaterRelation relation) {
        if (relation != null && relation.getStatus().equals(EaterRelation.STATUS_PENDING)
                && relation.getType().equals(EaterRelation.TYPE_FRIEND)) {
            relation.setStatus(EaterRelation.STATUS_IGNORED);
            return save(relation);
        } else {
            throw new YoueatException("Relation cannot be updated");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EaterRelation> getAllFollowUsers(Eater ofUser) {
        Criterion critUser = Restrictions.eq(EaterRelation.FROM_USER, ofUser);
        Criterion critType = Restrictions.eq(EaterRelation.TYPE, EaterRelation.TYPE_FOLLOW);
        Criterion critStatus = Restrictions.eq(EaterRelation.STATUS, EaterRelation.STATUS_ACTIVE);
        return findByCriteria(critUser, critType, critStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EaterRelation> getAllFriendUsers(Eater ofUser) {
        Criterion critUser = Restrictions.eq(EaterRelation.FROM_USER, ofUser);
        Criterion critType = Restrictions.eq(EaterRelation.TYPE, EaterRelation.TYPE_FRIEND);
        Criterion critStatus = Restrictions.eq(EaterRelation.STATUS, EaterRelation.STATUS_ACTIVE);
        return findByCriteria(critUser, critType, critStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EaterRelation> getAllRelations(Eater ofUser) {
        Conjunction pendingFriend = Restrictions.conjunction();
        pendingFriend.add(Restrictions.eq(EaterRelation.STATUS, EaterRelation.STATUS_PENDING));
        pendingFriend.add(Restrictions.eq(EaterRelation.TYPE, EaterRelation.TYPE_FRIEND));
        pendingFriend.add(Restrictions.eq(EaterRelation.TO_USER, ofUser));
        Disjunction inOr = Restrictions.disjunction();
        inOr.add(pendingFriend);
        inOr.add(Restrictions.eq(EaterRelation.FROM_USER, ofUser));
        List<EaterRelation> results = findByCriteria(inOr);
        Collections.sort(results);
        return results;
    }

    private List<EaterRelation> getOppositeRelation(EaterRelation relation) {
        Conjunction friend = Restrictions.conjunction();
        friend.add(Restrictions.eq(EaterRelation.TYPE, relation.getType()));
        friend.add(Restrictions.eq(EaterRelation.TO_USER, relation.getFromUser()));
        friend.add(Restrictions.eq(EaterRelation.FROM_USER, relation.getToUser()));
        return findByCriteria(friend);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EaterRelation> getAllActiveRelations(Eater ofUser) {
        Criterion critUser = Restrictions.eq(EaterRelation.FROM_USER, ofUser);
        Criterion critRelationActive = Restrictions.eq(EaterRelation.STATUS, EaterRelation.STATUS_ACTIVE);
        return findByCriteria(critUser, critRelationActive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(EaterRelation relation) {
        EaterRelation eaterRelation = getByID(relation.getId());
        super.remove(eaterRelation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation getRelation(Eater ofUser, Eater toUser) {
        Criterion critUser = Restrictions.eq(EaterRelation.FROM_USER, ofUser);
        Criterion critToUser = Restrictions.eq(EaterRelation.TO_USER, toUser);
        List<EaterRelation> relation = findByCriteria(critUser, critToUser);
        if (!relation.isEmpty()) {
            return relation.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EaterRelation> getAllPendingFriendRequetToUsers(Eater toUser) {
        Criterion critUser = Restrictions.eq(EaterRelation.TO_USER, toUser);
        Criterion critType = Restrictions.eq(EaterRelation.TYPE, EaterRelation.TYPE_FRIEND);
        Criterion critStatus = Restrictions.eq(EaterRelation.STATUS, EaterRelation.STATUS_PENDING);
        return findByCriteria(critUser, critType, critStatus);
    }
}