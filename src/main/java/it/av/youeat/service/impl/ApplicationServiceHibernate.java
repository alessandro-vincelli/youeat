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

import it.av.youeat.YoueatConcurrentModificationException;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.BasicEntity;
import it.av.youeat.service.ApplicationService;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 * @param <T>
 */
@Repository
public class ApplicationServiceHibernate<T extends BasicEntity> extends JpaDaoSupport implements ApplicationService<T> {

    /**
     * @param entityManager
     */
    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "youeatPersistence")
    public void setInternalEntityManager(final EntityManager entityManager) {
        setEntityManager(entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public T save(T obj) {
        if (obj == null) {
            throw new YoueatException("Object is null");
        }

        try {
            if (obj.getId() != null && !obj.getId().isEmpty()) {
                return (getJpaTemplate().getEntityManager().merge(obj));
            } else {
                getJpaTemplate().getEntityManager().persist(obj);
            }
            return obj;
        } catch (OptimisticLockingFailureException e) {
            throw new YoueatConcurrentModificationException(e);
        } catch (DataAccessException e) {
            throw new YoueatException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> getAll() {
        return findByCriteria();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public List<T> findFullText(String query) {
        throw new YoueatException("not implememted yet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void remove(T object) {
        try {
            T objectToRemove = getByID(object.getId());
            getJpaTemplate().getEntityManager().remove(objectToRemove);
        } catch (DataAccessException e) {
            throw new YoueatException(e);
        }
    }

    protected final Class<T> getPersistentClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> findByCriteria(Criterion... criterion) {
        return findByCriteria(getPersistentClass(), null, 0, 0, criterion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findByCriteria(Order order, int firstResult, int maxResults, Criterion... criterion) {
        return findByCriteria(getPersistentClass(), order, firstResult, maxResults, criterion);
    }

    @Transactional(readOnly = true)
    protected List<T> findByCriteria(Order order, Criterion... criterion) {
        return findByCriteria(getPersistentClass(), order, 0, 0, criterion);
    }

    @Transactional(readOnly = true)
    protected List<T> findByCriteria(Class<T> actualClass, Order order, int firstResult, int maxResults, Criterion... criterion) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        if (order != null) {
            criteria.addOrder(order);
        }
        for (Criterion c : criterion) {
            if(c != null){
                criteria.add(c);
            }
        }
        if (firstResult > 0) {
            criteria.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            criteria.setMaxResults(maxResults);
        }
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public T getByID(String id) {
        return getJpaTemplate().getEntityManager().find(getPersistentClass(), id);
    }

    protected final Session getHibernateSession() {
        return (Session) getJpaTemplate().getEntityManager().getDelegate();
    }

}
