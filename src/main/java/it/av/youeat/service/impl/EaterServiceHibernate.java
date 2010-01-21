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

import it.av.youeat.UserAlreadyExistsException;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.service.EaterService;
import it.av.youeat.util.LuceneUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class EaterServiceHibernate extends ApplicationServiceHibernate<Eater> implements EaterService {

    private StrongPasswordEncryptor passwordEncoder;
    @Autowired
    private EaterProfileService eaterProfileService;
    @Autowired
    private EaterRelationService eaterRelationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater addRegolarUser(Eater eater) {
        eater.setUserProfile(eaterProfileService.getRegolarUserProfile());
        try {
            return add(eater);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater addAdminUser(Eater eater) {
        eater.setUserProfile(eaterProfileService.getAdminUserProfile());
        try {
            return add(eater);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater add(Eater eater) {
        if (eater == null || StringUtils.isBlank(eater.getEmail())) {
            throw new YoueatException("Eater is null or email is empty");
        }
        eater.setPassword(passwordEncoder.encryptPassword(eater.getPassword()));
        eater.setCreationTime(DateUtil.getTimestamp());
        if (eater.getUserProfile() == null) {
            eater.setUserProfile(eaterProfileService.getRegolarUserProfile());
        }
        return super.save(eater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater update(Eater object) {
        try {
            super.save(object);
        } catch (DataAccessException e) {
            throw new YoueatException(e);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater getByEmail(String email) {
        Criterion crit = Restrictions.eq(Eater.EMAIL, email);
        List<Eater> result = super.findByCriteria(crit);
        if (result != null && result.size() == 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Eater> findUserWithoutRelation(Eater forUser, String pattern) {
        StringBuffer searchPattern = new StringBuffer("-id:(");
        searchPattern.append(forUser.getId());
        searchPattern.append(" ");
        // collect eater already friends to exclude them from the results
        Collection<EaterRelation> relatedUser = eaterRelationService.getAllRelations(forUser);
        ArrayList<String> relatedUserId = new ArrayList<String>(relatedUser.size());
        for (EaterRelation userRelation : relatedUser) {
            relatedUserId.add(userRelation.getToUser().getId());
            searchPattern.append(userRelation.getToUser().getId());
            searchPattern.append(" ");
        }
        searchPattern.append(")");
        // use the search pattern on the firstname and lastname
        if (StringUtils.isNotBlank(pattern)) {
            String patternClean = LuceneUtil.escapeSpecialChars(pattern);
            patternClean = patternClean + "~";
            searchPattern.append(" %% (");
            searchPattern.append(" firstname:(");
            searchPattern.append(pattern);
            searchPattern.append(") || ");
            searchPattern.append(" lastname:(");
            searchPattern.append(pattern);
            searchPattern.append(")) ");
        }

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getJpaTemplate().getEntityManager());

        QueryParser queryParser = new QueryParser("", fullTextEntityManager.getSearchFactory().getAnalyzer(
                "ristoranteanalyzer"));

        org.apache.lucene.search.Query query;
        try {
            query = queryParser.parse(searchPattern.toString());
        } catch (Exception e) {
            throw new YoueatException(e);
        }
        javax.persistence.Query persistenceQuery = fullTextEntityManager.createFullTextQuery(query, Eater.class);
        return persistenceQuery.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Eater> findUserWithoutRelation(Eater forUser) {
        return findUserWithoutRelation(forUser, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Eater user) {
        super.remove(user);
    }

    public void setPasswordEncoder(StrongPasswordEncryptor passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserProfileService(EaterProfileService userProfileService) {
        this.eaterProfileService = userProfileService;
    }

    public void setUserRelationService(EaterRelationService userRelationService) {
        this.eaterRelationService = userRelationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Eater> find(String pattern) {
        Criterion critByName = Restrictions.ilike(Eater.LASTNAME, pattern);
        List<Eater> results = findByCriteria(critByName);
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexData() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
                .getFullTextEntityManager(getJpaTemplate().getEntityManager());
        Collection<Eater> ristos = getAll();
        int position = 0;
        for (Eater risto : ristos) {
            fullTextEntityManager.index(risto);
            position = position + 1;
        }
    }

}