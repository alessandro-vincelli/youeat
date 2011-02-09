/**
 * Copyright 2011 the original author or authors
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
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.service.EaterService;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 
 * Custom {@link AuthenticationSuccessHandler} that create a new {@link OpenIDAuthenticationToken} that contains as a principal an
 * instance of {@link UserDetailsImpl} populated with "firstName", "lastName", "email" extracted from the available
 * {@link OpenIDAttribute}. The new OpenIDAuthenticationToken is set on the {@link SecurityContextHolder} overriding the previous
 * one.
 * 
 * @author Alessandro Vincelli
 * 
 */
public class YouEatAuthenticationSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements
        AuthenticationSuccessHandler {

    private OpenIDAttributes2UserDetails attributes2UserDetails;
    @Autowired
    private EaterProfileService eaterProfileService;
    @Autowired
    private EaterService eaterService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OpenIDAuthenticationToken auth = (OpenIDAuthenticationToken) authentication;
        UserDetails details = ((UserDetails) auth.getPrincipal());

        // if the user is not yet present as Google Account
        // before check if ther'is a user with the same email already on the DB, then update the user assigning a SocialUID an
        // transforming the user in a SocialType.GOOGLE
        // otherwise insert the Google user in the DB as a new User
        if (details.getUsername() == null) {
            Eater user = attributes2UserDetails.extract((OpenIDAuthenticationToken) authentication);
            Eater userOnTheDB = eaterService.getByEmail(user.getEmail());
            if (userOnTheDB != null) {
                userOnTheDB.setSocialUID(user.getSocialUID());
                userOnTheDB.setSocialType(SocialType.GOOGLE);
                details = new UserDetailsImpl(eaterService.update(userOnTheDB));
            } else {
                user.setUserProfile(eaterProfileService.getRegolarUserProfile());
                Eater googleUser = eaterService.addGoogleUser(user);
                details = new UserDetailsImpl(googleUser);
            }
        }

        OpenIDAuthenticationToken openIDAuthenticationToken = new OpenIDAuthenticationToken(details, details.getAuthorities(),
                auth.getIdentityUrl(), auth.getAttributes());
        SecurityContextHolder.getContext().setAuthentication(openIDAuthenticationToken);
        handle(request, response, authentication);
    }

    public void setAttributes2UserDetails(OpenIDAttributes2UserDetails attributes2UserDetails) {
        this.attributes2UserDetails = attributes2UserDetails;
    }

}
