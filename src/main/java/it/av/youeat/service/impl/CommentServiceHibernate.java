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

import it.av.youeat.ocm.model.Comment;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.CommentService;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment service
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Repository
@Transactional(readOnly = true)
public class CommentServiceHibernate extends ApplicationServiceHibernate<Comment> implements CommentService {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeByEater(Eater eater) {
        remove(getByEater(eater));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Comment> getByEater(Eater eater) {
        Criterion critByEater = Restrictions.eq(Comment.AUTHOR_FIELD, eater);
        return findByCriteria(critByEater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void remove(Collection<Comment> comments) {
        for (Comment comment : comments) {
            remove(comment);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Comment> find(String pattern, int first, int maxResults, String sortField, boolean isAscending) {
        Criterion critByTitle = null;
        Criterion critByBody = null;
        //Criterion critByLastname = null;
        //Criterion critByFirstname = null;
        if (StringUtils.isNotBlank(pattern)) {
            //critByFirstname = Restrictions.ilike(Comment.AUTHOR_FIELD + ".firstname", pattern);
            //critByLastname = Restrictions.ilike(Comment.AUTHOR_FIELD + ".lastname", pattern);
            critByTitle = Restrictions.ilike(Comment.TITLE_FIELD, pattern);
            critByBody = Restrictions.ilike(Comment.BODY_FIELD, pattern);
        }

        Disjunction critInOr = Restrictions.disjunction();
        //critInOr.add(critByFirstname);
        //critInOr.add(critByLastname);
        critInOr.add(critByBody);
        critInOr.add(critByTitle);

        Order order = null;
        if (isAscending) {
            order = Order.asc(sortField);
        } else {
            order = Order.desc(sortField);
        }
        return findByCriteria(order, first, maxResults, critInOr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment disable(Comment comment) {
        comment = getByID(comment.getId());
        comment.setEnabled(false);
        return save(comment);
    }

}