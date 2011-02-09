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
package it.av.youeat.web.security;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.SocialType;
import it.av.youeat.service.EaterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class UserDetailsServiceOpenIDImpl implements UserDetailsService {

    @Autowired
    private EaterService service;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String openidUID) throws UsernameNotFoundException, DataAccessException {

        Eater user = service.getBySocialUID(openidUID, SocialType.GOOGLE);
        if (user != null) {
            return new UserDetailsImpl(user);
        } else {
            return new UserDetailsImpl();
        }
    }

}
