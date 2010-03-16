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

import org.hibernate.criterion.Criterion;
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
@Transactional
public class CommentServiceHibernate extends ApplicationServiceHibernate<Comment> implements CommentService {

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeByEater(Eater eater) {
        remove(getByEater(eater));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<Comment> getByEater(Eater eater) {
        Criterion critByEater = Restrictions.eq(Comment.AUTHOR_FIELD, eater);
        return findByCriteria(critByEater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Collection<Comment> comments) {
        for (Comment comment : comments) {
            remove(comment);
        }
    }

}