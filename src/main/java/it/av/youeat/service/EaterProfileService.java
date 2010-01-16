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

import it.av.youeat.ocm.model.EaterProfile;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services on the Eater Profile
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Service
public interface EaterProfileService {
    /**
     * Save a EaterProfile
     * 
     * @param object
     * @return just saved profile
     */
    @Transactional
    EaterProfile save(EaterProfile object);

    /**
     * Get all the user profile
     * 
     * @return all the user profile
     */
    @Transactional(readOnly = true)
    Collection<EaterProfile> getAll();

    /**
     * Remove a profile
     * 
     * @param profile
     */
    @Transactional
    void remove(EaterProfile profile);

    /**
     * Return the regular user profile, it must be the "USER" profile
     * 
     * @return EaterProfile
     */
    EaterProfile getRegolarUserProfile();

    /**
     * Return the user with the passed name, there's a unique constraint on the user profile name
     * 
     * @param id
     * @return the user with the given name
     */
    @Transactional(readOnly = true)
    EaterProfile getByName(String id);

}