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
package it.av.eatt.service.impl;

import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.EaterRelation;
import it.av.eatt.ocm.util.DateUtil;
import it.av.eatt.service.EaterRelationService;

import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class EaterRelationServiceHibernate extends ApplicationServiceHibernate<EaterRelation> implements
        EaterRelationService {

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation addFollowUser(Eater fromUser, Eater toUser) {
        EaterRelation relation = EaterRelation.createFollowRelation(fromUser, toUser);
        return save(relation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterRelation addFriendRequest(Eater fromUser, Eater toUser) {
        EaterRelation relation = EaterRelation.createFriendRelation(fromUser, toUser);
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
            return save(relation);
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
        return findByCriteria(inOr);
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
}