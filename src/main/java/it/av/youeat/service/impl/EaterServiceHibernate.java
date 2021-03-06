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
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.model.SocialType;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.ActivityRelationService;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.CommentService;
import it.av.youeat.service.DialogService;
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.system.MailService;
import it.av.youeat.util.LuceneUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class EaterServiceHibernate extends ApplicationServiceHibernate<Eater> implements EaterService {

    @Autowired
    private MessageDigestPasswordEncoder passwordEncoder;
    @Autowired
    private EaterProfileService eaterProfileService;
    @Autowired
    private EaterRelationService eaterRelationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ActivityRistoranteService activityRistoranteService;
    @Autowired
    private ActivityRelationService activityRelationService;
    @Autowired
    private DialogService dialogService;
    @Autowired
    private CommentService commentService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Eater addRegolarUser(Eater eater) {
        eater.setUserProfile(eaterProfileService.getRegolarUserProfile());
        try {
            return add(eater);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void remove(Eater eater) {

        Eater eaterToRemove = getByID(eater.getId());
        activityRelationService.removeByEater(eater);
        activityRistoranteService.removeByEater(eater);
        commentService.removeByEater(eater);
        dialogService.removeByEater(eater);
        eaterRelationService.removeByEater(eater);
        super.remove(eaterToRemove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Eater addAdminUser(Eater eater) {
        eater.setUserProfile(eaterProfileService.getAdminUserProfile());
        try {
            return add(eater);
        } catch (ConstraintViolationException e) {
            throw new UserAlreadyExistsException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Eater add(Eater eater) {
        if (eater == null || StringUtils.isBlank(eater.getEmail())) {
            throw new YoueatException("Eater is null or email is empty");
        }
        eater.setPasswordSalt(UUID.randomUUID().toString());
        eater.setPassword(passwordEncoder.encodePassword(eater.getPassword(), eater.getPasswordSalt()));
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
    @Transactional
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
        // exclude also the admin users
        Collection<Eater> adminUsers = getAllAdminUsers();
        for (Eater admin : adminUsers) {
            relatedUserId.add(admin.getId());
            searchPattern.append(admin.getId());
            searchPattern.append(" ");
        }
        searchPattern.append(")");
        // use the search pattern on the firstname and lastname
        if (StringUtils.isNotBlank(pattern)) {
            String patternClean = LuceneUtil.escapeSpecialChars(pattern);
            String patternFuzzy = LuceneUtil.fuzzyAllTerms(patternClean);
            searchPattern.append(" %% (");
            searchPattern.append(" firstname:");
            searchPattern.append(patternFuzzy);
            searchPattern.append(" || ");
            searchPattern.append(" lastname:");
            searchPattern.append(patternFuzzy);
            searchPattern.append(") ");
        }

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());

        QueryParser queryParser = new QueryParser(Version.LUCENE_31, "", fullTextEntityManager.getSearchFactory().getAnalyzer("ristoranteanalyzer"));

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
        return findByCriteria(critByName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void indexData() {
        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(getJpaTemplate()
                .getEntityManager());
        Collection<Eater> ristos = getAll();
        int position = 0;
        for (Eater risto : ristos) {
            fullTextEntityManager.index(risto);
            position = position + 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return passwordEncoder.isPasswordValid(encPass, rawPass, salt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodePassword(String rawPass, Object salt) {
        return passwordEncoder.encodePassword(rawPass, salt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPasswordByEmail(Eater eater, String newPassword) {
        mailService.sendPassword(eater, newPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater setRandomPassword(Eater eater) {
        eater.setPassword(encodePassword(UUID.randomUUID().toString().substring(0, 8), eater.getPasswordSalt()));
        return update(eater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater getBySocialUID(String uid, SocialType socialType) {
        Criterion critById = Restrictions.eq(Eater.SOCIALUID, uid);
        Criterion critBySt = Restrictions.eq(Eater.SOCIALTYPE, socialType);
        List<Eater> result = super.findByCriteria(critById, critBySt);
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
    @Transactional
    public Eater addFacebookUser(Eater eater) {
        if (StringUtils.isBlank(eater.getSocialUID())) {
            throw new YoueatException("Impossible add a facebook user without a socialUid");
        }
        eater.setSocialType(SocialType.FACEBOOK);
        eater.setEmail(eater.getSocialUID() + "-disabled@youeat.org");
        return addRegolarUser(eater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Eater addGoogleUser(Eater eater) {
        if (StringUtils.isBlank(eater.getSocialUID())) {
            throw new YoueatException("Impossible add a google user without a socialUid");
        }
        eater.setSocialType(SocialType.GOOGLE);
        return addRegolarUser(eater);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Eater> getAllAdminUsers() {
        EaterProfile profile = eaterProfileService.getAdminUserProfile();
        Criterion critByAdmin = Restrictions.eq(Eater.USERPROFILE, profile);
        return super.findByCriteria(critByAdmin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int count() {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.uniqueResult()).intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Eater> find(String pattern, int firstResult, int maxResults, String sortField, boolean isAscending) {
        Criterion critByName = null;
        if(StringUtils.isNotBlank(pattern)){
            critByName = Restrictions.ilike(Eater.LASTNAME, pattern);
        }
        Order order = null;
        if(isAscending){
            order = Order.asc(sortField);
        }
        else{
            order = Order.desc(sortField);
        }
        return findByCriteria(order, firstResult, maxResults, critByName);
    }

    @Override
    public int count(String pattern) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        Criterion critByName = Restrictions.ilike(Eater.LASTNAME, pattern);
        criteria.setProjection(Projections.rowCount());
        if(StringUtils.isNotBlank(pattern)){
            criteria.add(critByName);
        }
        return ((Long) criteria.uniqueResult()).intValue();
    }
}