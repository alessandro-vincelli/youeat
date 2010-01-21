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
import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.ProvIta;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.DataRistoranteService;
import it.av.youeat.util.LuceneUtil;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;

/**
 * Implements the operation on {@link DataRistorante}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class DataRistoranteServiceHibernate extends ApplicationServiceHibernate<DataRistorante> implements
        DataRistoranteService {

    /**
     * @param entityManager
     */
    // @PersistenceContext(type = PersistenceContextType.TRANSACTION, unitName="staticDataPersistance")
    // @Override
    // public void setInternalEntityManager(final EntityManager entityManager) {
    // super.setInternalEntityManager(entityManager);
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataRistorante update(DataRistorante risto) {
        super.save(risto);
        getJpaTemplate().flush();
        return risto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataRistorante insert(DataRistorante risto) {
        super.save(risto);
        return risto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<DataRistorante> find(String pattern, int maxResults) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, pattern, MatchMode.ANYWHERE);
        Order orderByName = Order.asc(DataRistorante.NAME);
        return findByCriteria(orderByName, 0, maxResults, critByName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(DataRistorante object) {
        super.remove(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProvIta> getAllProv() {
        Criteria criteria = getHibernateSession().createCriteria(ProvIta.class);
        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<DataRistorante> find(String pattern, String city, Country country, int maxResults) {
        Criterion critByName = Restrictions.ilike(Ristorante.NAME, pattern, MatchMode.ANYWHERE);
        Criterion critByCity = Restrictions.eq(Ristorante.CITY, city);
        Criterion critByCountry = Restrictions.eq(Ristorante.COUNTRY, country.getIso2());
        Order orderByName = Order.asc(DataRistorante.NAME);
        return findByCriteria(orderByName, 0, maxResults, critByName, critByCity, critByCountry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<DataRistorante> getBy(String pattern, String city, Country country) {
        Criterion critByName = Restrictions.eq(Ristorante.NAME, pattern);
        Criterion critByCity = Restrictions.eq(Ristorante.CITY, city);
        Criterion critByCountry = Restrictions.eq(Ristorante.COUNTRY, country.getIso2());
        List<DataRistorante> results = findByCriteria(critByName, critByCity, critByCountry);
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataRistorante> freeTextSearch(String pattern, int maxResult) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getJpaTemplate().getEntityManager());
        String[] fields = new String[] { "name" };
        // TODO using dataristoranteanalyzer doesn't work
        // MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, fullTextEntityManager.getSearchFactory()
        // .getAnalyzer("ristoranteanalyzer"));
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
        org.apache.lucene.search.Query query;
        try {
            query = parser.parse(LuceneUtil.escapeSpecialChars(pattern));
        } catch (ParseException e) {
            throw new YoueatException(e);
        }
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query,
                DataRistorante.class);
        if (maxResult > 0) {
            persistenceQuery.setMaxResults(maxResult);
        }
        return persistenceQuery.getResultList();
    }

    @Override
    public List<DataRistorante> freeTextSearch(String pattern, String city, String country, int maxResult) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getJpaTemplate().getEntityManager());
        StringBuffer searchPattern = new StringBuffer();
        // use the search pattern on the name, city and country are mandatory match
        if (StringUtils.isNotBlank(pattern)) {
            String patternClean = LuceneUtil.escapeSpecialChars(pattern);
            searchPattern.append(" name:(");
            patternClean = patternClean + "~";
            searchPattern.append(patternClean);
            searchPattern.append(") && ");
            searchPattern.append(" +city:(");
            searchPattern.append(city);
            searchPattern.append(") && ");
            searchPattern.append(" +country:(");
            searchPattern.append(country);
            searchPattern.append(") ");
        }

        // TODO using dataristoranteanalyzer doesn't work
        // QueryParser queryParser = new QueryParser("", fullTextEntityManager.getSearchFactory().getAnalyzer(
        // "dataristoranteanalyzer"));
        QueryParser queryParser = new QueryParser("", new StandardAnalyzer());

        org.apache.lucene.search.Query query;
        try {
            query = queryParser.parse(searchPattern.toString());
        } catch (Exception e) {
            throw new YoueatException(e);
        }
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query,
                DataRistorante.class);
        if (maxResult > 0) {
            persistenceQuery.setMaxResults(maxResult);
        }
        return persistenceQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexData() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getJpaTemplate().getEntityManager());
        fullTextEntityManager.getSearchFactory().getAnalyzer("dataristoranteanalyzer");
        Collection<DataRistorante> ristos = getAll();
        int size = ristos.size();
        int position = 0;
        for (DataRistorante risto : ristos) {
            fullTextEntityManager.index(risto);
            position = position + 1;
            System.out.println(position);
        }
    }
}