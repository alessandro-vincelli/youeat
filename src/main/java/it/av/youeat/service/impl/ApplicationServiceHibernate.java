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
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 * @param <T>
 */
public class ApplicationServiceHibernate<T extends BasicEntity> extends JpaDaoSupport implements ApplicationService<T> {

    /**
     * @param entityManager
     */
    @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName = "mainPersistance")
    public void setInternalEntityManager(final EntityManager entityManager) {
        setEntityManager(entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T save(T obj) {
        if (obj == null) {
            throw new YoueatException("Object is null");
        }

        try {
            if (obj.getId() != null && !obj.getId().isEmpty()) {
                getJpaTemplate().merge(obj);
            } else {
                getJpaTemplate().persist(obj);
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
    public List<T> getAll() {
        return findByCriteria();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public List<T> findFullText(String query) {
        /*        //DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
                DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
                //return getHibernateTemplate().findByCriteria(criteria, 0, -1);
                //TODO implement a default search strategy
                FullTextSession fullTextSession = Search.getFullTextSession(getSession(false));
                org.apache.lucene.queryParser.QueryParser parser = new QueryParser("tag", new StopAnalyzer() );

                org.apache.lucene.search.Query luceneQuery;
                try {
                    String[] ddd = new String[1];
                    ddd[0] = "tag";
                    parser = new MultiFieldQueryParser( ddd, new StandardAnalyzer());
                    Term t = new Term("tag", "*tag1*");
                    TermQuery query2 = new TermQuery(t);
                    parser.setAllowLeadingWildcard( true );
                    Query queryinutile= parser.parse(query);
                    org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery( query2, getPersistentClass() );
                    
                    List<T> result = fullTextQuery.list();
                    return result;
                } catch (ParseException e) {
                    throw new JackWicketException(e);
                }*/
        throw new YoueatException("not implememted yet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(T object) {
        try {
            getJpaTemplate().remove(object);
            // getJpaTemplate().flush();
        } catch (DataAccessException e) {
            throw new YoueatException(e);
        }
    }

    public Class<T> getPersistentClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findByCriteria(Criterion... criterion) {
        return findByCriteria(getPersistentClass(), null, 0, 0, criterion);
    }

    @Override
    public List<T> findByCriteria(Order order, int firstResult, int maxResults, Criterion... criterion) {
        return findByCriteria(getPersistentClass(), order, firstResult, maxResults, criterion);
    }

    protected List<T> findByCriteria(Order order, Criterion... criterion) {
        return findByCriteria(getPersistentClass(), order, 0, 0, criterion);
    }

    protected List<T> findByCriteria(Class<T> actualClass, Order order, int firstResult, int maxResults,
            Criterion... criterion) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        if (order != null) {
            criteria.addOrder(order);
        }
        for (Criterion c : criterion) {
            criteria.add(c);
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
    public T getByID(String id) {
        Criterion crit = Restrictions.idEq(id);
        return findByCriteria(crit).iterator().next();
    }

    protected Session getHibernateSession() {
        return (Session) getJpaTemplate().getEntityManager().getDelegate();
    }

}
