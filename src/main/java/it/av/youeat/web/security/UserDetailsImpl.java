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

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Provides user information 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class UserDetailsImpl implements UserDetails {

    private String passwordSalt;
    private Eater user;

    /**
     * Constructor
     * 
     * @param user
     */
    public UserDetailsImpl(Eater user) {
        this.user = user;
        this.passwordSalt = user.getPasswordSalt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        GrantedAuthority ga = new GrantedAuthorityImpl(user.getUserProfile().getName());
        ArrayList<GrantedAuthority> gaL = new ArrayList<GrantedAuthority>(1);
        gaL.add(ga);
        return gaL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return the logged user
     */
    public final Eater getUser() {
        return user;
    }

    /**
     * @return th password salt
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

}
