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
import it.av.youeat.ocm.model.Tag;
import it.av.youeat.service.TagService;
import it.av.youeat.util.LuceneUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Transactional(readOnly = true)
@Repository
public class TagServiceHibernate extends ApplicationServiceHibernate<Tag> implements TagService {



    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Tag insert(String tag) {
        Tag result = getByTagValue(tag);
        if (result != null) {
            return result;
        } else {
            return super.save(new Tag(tag));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> find(String query) {
        Criterion crit = Restrictions.ilike(Tag.TAG, query);
        return findByCriteria(crit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag getByTagValue(String tagValue) {
        Criterion crit = Restrictions.eq(Tag.TAG, tagValue);
        List<Tag> results = findByCriteria(crit);
        if (results.size() == 1) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> freeTextSearch(String pattern) {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());
        String[] fields = new String[] { "tag" };
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer(Version.LUCENE_CURRENT));
        org.apache.lucene.search.Query query;
        try {
            String patternClean = LuceneUtil.escapeSpecialChars(pattern);
            String patternFuzzy = LuceneUtil.fuzzyAllTerms(patternClean);
            query = parser.parse(patternFuzzy);
        } catch (ParseException e) {
            throw new YoueatException(e);
        }
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Tag.class);
        return persistenceQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getTagsAndScore() {
        SQLQuery criteria = getHibernateSession().createSQLQuery("select tag, count(tags) from ristorante_tags inner join tag on (tags = id)  group by tags, tag order by tag;");
        List list = criteria.list();
        if (list != null && list.size() > 0) {
            Map<String, Integer> map = new HashMap<String, Integer>(list.size());
            for (Object object : list) {
                map.put((String) ((Object[]) object)[0], ((BigInteger) ((Object[]) object)[1]).intValue());
            }
            return map;
        } else {
            return new HashMap<String, Integer>();
        }
    }
}