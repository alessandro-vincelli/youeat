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

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CityService;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Implements the operation on {@link City}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class CityServiceHibernate extends ApplicationServiceHibernate<City> implements CityService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<City> getAll() {
        Order orderBYName = Order.asc(City.NAME_FIELD);
        return super.findByCriteria(orderBYName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<City> find(String string, Country country, int maxResults) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, string + "%");
        Criterion byCountry = Restrictions.eq(City.COUNTRY_FIELD, country);
        Order orderByName = Order.asc(City.NAME_FIELD);
        return findByCriteria(orderByName, 0, maxResults, critByName, byCountry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<City> find(String string, int maxResults) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, string + "%");
        Order orderByName = Order.asc(City.NAME_FIELD);
        return findByCriteria(orderByName, 0, maxResults, critByName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findName(String string, Country country, int maxResults) {
        Query query = getJpaTemplate().getEntityManager().createQuery(
                "select name  from City as city where upper(city.name) like upper(:name) and city.country = :country");
        query.setParameter("name", string + "%");
        query.setParameter("country", country);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findName(String string, int maxResults) {
        Query query = getJpaTemplate().getEntityManager().createQuery(
                "select name  from City as city where upper(city.name) like upper(:name)");
        query.setParameter("name", string + "%");
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public City getByNameAndCountry(String cityName, Country country) {
        Query query = getJpaTemplate().getEntityManager().createQuery(
                "select city from City as city where upper(city.name) = upper(:name) and city.country = :country");
        query.setParameter("name", cityName);
        query.setParameter("country", country);
        query.setMaxResults(1);
        List<City> resuts = query.getResultList();
        if (resuts.isEmpty()) {
            return null;
        } else {
            return resuts.get(0);
        }
    }
}