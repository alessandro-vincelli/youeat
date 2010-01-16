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
package it.av.youeat.service;

import it.av.youeat.ocm.model.Activity;
import it.av.youeat.ocm.model.Eater;

import java.util.Collection;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operations on {@Link Activity}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Service
@Transactional
public interface ActivityService extends ApplicationService<Activity> {

    /**
     * {@inheritDoc}
     */
    @Transactional
    Activity save(Activity object);

    /**
     * Return the activity on the given date
     * 
     * @param date
     * @return activities on the given date
     */
    @Transactional(readOnly = true)
    Collection<Activity> findByDate(Date date);

    /**
     * Find activities of the given user
     * 
     * @param user
     * @return activities for the given user
     */
    @Transactional(readOnly = true)
    Collection<Activity> findByUser(Eater user);

    /**
     * {@inheritDoc}
     */
    @Transactional
    void remove(Activity object);

}