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

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class AuthenticationProvider {
    private static DaoAuthenticationProvider authenticationProvider;
    private static FacebookAuthenticationProvider facebookAuthenticationProvider;

    private AuthenticationProvider(DaoAuthenticationProvider authenticationProvider,
            FacebookAuthenticationProvider facebookAuthenticationProvider) {
        AuthenticationProvider.authenticationProvider = authenticationProvider;
        AuthenticationProvider.facebookAuthenticationProvider = facebookAuthenticationProvider;
    }

    public static Authentication authenticate(String username, String password) {
        return authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    public static Authentication faceBookAuthenticate(HttpServletRequest request) {
        return facebookAuthenticationProvider.authenticate(new FacebookAuthenticationToken(request));
    }
}
