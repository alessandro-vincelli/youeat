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

import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.service.EaterProfileService;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Repository
@Transactional(readOnly = true)
public class EaterProfileServiceHibernate extends ApplicationServiceHibernate<EaterProfile> implements EaterProfileService {

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterProfile getRegolarUserProfile() {
        return getByName(EaterProfile.USER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterProfile getAdminUserProfile() {
        return getByName(EaterProfile.ADMIN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EaterProfile getByName(String name) {
        Criterion crit = Restrictions.eq(EaterProfile.NAME, name);
        List<EaterProfile> result = super.findByCriteria(crit);
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }
}