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
import it.av.youeat.service.CountryService;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the operation on {@link City}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Transactional(readOnly = true)
@Repository
public class CityServiceHibernate extends ApplicationServiceHibernate<City> implements CityService {

    @Autowired
    private CountryService countryService;

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
    public List<City> find(String string, int maxResults) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, string + "%");
        Order orderByName = Order.asc(City.NAME_FIELD);
        return findByCriteria(orderByName, 0, maxResults, critByName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findByName(String string, Country country, int maxResults) {
        Query query = getJpaTemplate()
                .getEntityManager()
                .createQuery(
                        "select name  from City as city where upper(city.name) like upper(:name) and city.country = :country order by length(city.name)");
        query.setParameter("name", string + "%");
        query.setParameter("country", country);
        query.setMaxResults(maxResults);
        return query.getResultList();
        // FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
        // .getFullTextEntityManager(getJpaTemplate().getEntityManager());
        // QueryParser queryParser = new QueryParser("", new StandardAnalyzer());
        // org.apache.lucene.search.Query query;
        //        
        // StringBuffer searchPattern = new StringBuffer();
        // // use the search pattern on the name, city and country are mandatory match
        // if (StringUtils.isNotBlank(string)) {
        // String cityCleaned = LuceneUtil.escapeSpecialChars(string);
        // String countryCleaned = LuceneUtil.escapeSpecialChars(country.getName());
        // searchPattern.append(" name:(");
        // searchPattern.append(cityCleaned);
        // searchPattern.append(") && ");
        // searchPattern.append(" +country.name:(");
        // searchPattern.append(countryCleaned);
        // searchPattern.append(")");
        // }
        // try {
        // query = queryParser.parse(searchPattern.toString());
        // } catch (ParseException e) {
        // throw new YoueatException(e);
        // }
        // javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query,
        // City.class);
        // return persistenceQuery.getResultList();
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void indexData() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());
        Collection<City> cities = getAll();
        int position = 0;
        for (City city : cities) {
            fullTextEntityManager.index(city);
            position = position + 1;
        }
        // System.out.println("city indexed");
        // TODO to much data improve the indexing method
        Collection<Country> countries = countryService.getAll();
        position = 0;
        for (Country country : countries) {
            fullTextEntityManager.index(country);
            position = position + 1;
        }
        // System.out.println("country indexed");
    }

}