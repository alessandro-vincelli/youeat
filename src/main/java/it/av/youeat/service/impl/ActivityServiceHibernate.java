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

import it.av.youeat.ocm.model.Activity;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.ActivityService;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * UserImpl Services class, Use this class to manage the {@link UserImpl}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ActivityServiceHibernate extends ApplicationServiceHibernate<Activity> implements ActivityService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Activity> findByDate(Date date){
        Criterion crit = Restrictions.eq(Activity.DATE, date);
        return findByCriteria(crit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Activity> findByUser(Eater user){
        Criterion crit = Restrictions.eq(Activity.USER, user);
        return findByCriteria(crit);
    }
}