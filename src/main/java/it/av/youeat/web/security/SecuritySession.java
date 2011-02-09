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

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implements the authentication strategies
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SecuritySession extends AuthenticatedWebSession {
    /**
     * Construct.
     * 
     * @param request The current request object
     */
    public SecuritySession(Request request) {
        super(request);
    }

    private Authentication auth;
    private String username = "";
    private Roles roles;

    /**
     * @see org.apache.wicket.authentication.AuthenticatedWebSession#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public boolean authenticate(final String username, final String password) {
        // Check username and password
        try {
            auth = AuthenticationProvider.authenticate(username, password);
            SecurityContextHolder.getContext().setAuthentication(auth);
            signIn(true);
            return auth.isAuthenticated();
        } catch (BadCredentialsException e) {
            // in general this error on a not existing user
            return false;
        }
    }

    public boolean authenticate(HttpServletRequest request) {
        // Check if the request contains a facebook session key
        try {
            auth = AuthenticationProvider.faceBookAuthenticate(request);
            if (!auth.isAuthenticated()) {
                return false;
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            signIn(true);
            return true;
        } catch (BadCredentialsException e) {
            // in general this error on a not existing user
            return false;
        }
    }

    /**
     * @see org.apache.wicket.authentication.AuthenticatedWebSession#getRoles()
     */
    @Override
    public Roles getRoles() {
        if (roles == null && SecurityContextHelper.getAuthenticatedUser() != null &&  SecurityContextHelper.isAuthenticatedUser() ) {
            roles = new Roles(SecurityContextHelper.getAuthenticatedUserDetails().getAuthorities().iterator().next()
                    .getAuthority());
        }
        return roles;
    }

    /**
     * @return the auth
     */
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    public Eater getLoggedInUser() {
        return SecurityContextHelper.getAuthenticatedUser();
    }

}