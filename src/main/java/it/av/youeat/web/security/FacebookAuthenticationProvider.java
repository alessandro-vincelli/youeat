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
import it.av.youeat.service.CountryService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.LanguageService;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookJaxbRestClient;
import com.google.code.facebookapi.ProfileField;
import com.google.code.facebookapi.schema.User;
import com.google.code.facebookapi.schema.UsersGetInfoResponse;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class FacebookAuthenticationProvider extends ProviderManager {
    @Autowired
    private EaterService eaterService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private FaceBookAuthHandler bookAuthHandler;

    private static Logger log = LoggerFactory.getLogger(FacebookAuthenticationProvider.class);

    /**
     * {@inheritDoc}
     */
    protected Authentication doAuthentication(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request = (HttpServletRequest) authentication.getPrincipal();

        FacebookJaxbRestClient authClient;
        try {
            authClient = bookAuthHandler.getAuthenticatedClient(request);
            String facebookSession = authClient.getCacheSessionKey();
            long facebookUserId = authClient.users_getLoggedInUser();

            Eater eater = eaterService.getBySocialUID(Long.toString(facebookUserId), SocialType.FACEBOOK);
            checkAndCreateUser(authClient, facebookUserId, eater);

            eater = eaterService.getBySocialUID(Long.toString(facebookUserId), SocialType.FACEBOOK);
            eater.setSocialSessionKey(facebookSession);
            Authentication authenticationToReturn = new FacebookAuthenticationToken(new UserDetailsImpl(eater));
            authenticationToReturn.setAuthenticated(true);
            return authenticationToReturn;
        } catch (Exception e) {
            log.info("Facebook session not available");
        }
        return authentication;

    }

    /**
     * @param authClient
     * @param facebookUserId
     * @param eater
     * @throws FacebookException
     */
    protected void checkAndCreateUser(FacebookJaxbRestClient authClient, long facebookUserId, Eater eater)
            throws FacebookException {
        if (eater == null) {
            ArrayList<Long> ids = new ArrayList<Long>();
            ids.add(facebookUserId);
            ArrayList<ProfileField> fields = new ArrayList<ProfileField>();
            fields.add(ProfileField.FIRST_NAME);
            fields.add(ProfileField.LAST_NAME);
            fields.add(ProfileField.LOCALE);
            fields.add(ProfileField.PIC);

            UsersGetInfoResponse users = authClient.users_getInfo(ids, fields);
            User user = users.getUser().get(0);
            eater = new Eater();
            eater.setFirstname(user.getFirstName());
            eater.setLastname(user.getLastName());
            eater.setSocialUID(Long.toString(facebookUserId));
            byte[] avatar = getTheAvatar(user.getPic());
            if (avatar != null) {
                eater.setAvatar(avatar);
            }
            eater.setCountry(countryService.getByIso2(user.getLocale().substring(3, 5)));
            eater.setLanguage(languageService.getSupportedLanguage(new Locale(user.getLocale())));
            eaterService.addFacebookUser(eater);
        }
    }

    /**
     * tries to get the user avatar from FB
     * 
     * @param avatarUrl
     * @return a user avatar
     */
    private byte[] getTheAvatar(String avatarUrl) {
        URL url;
        try {
            url = new URL(avatarUrl);
            InputStream is = url.openStream();
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            log.warn("impossible get The avatar from facebook", e);
        }
        return null;
    }

}
