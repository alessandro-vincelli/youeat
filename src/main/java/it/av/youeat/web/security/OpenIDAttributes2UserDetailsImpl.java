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
import it.av.youeat.service.CountryService;
import it.av.youeat.service.LanguageService;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

public class OpenIDAttributes2UserDetailsImpl implements OpenIDAttributes2UserDetails {
    
    @Autowired
    private CountryService countryService;
    @Autowired
    private LanguageService languageService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Eater extract(OpenIDAuthenticationToken token) {
        Eater user = new Eater();
        List<OpenIDAttribute> attributes = token.getAttributes();
        for (OpenIDAttribute openIDAttribute : attributes) {
            if (openIDAttribute.getName().equals("firstName")) {
                user.setFirstname(StringUtils.join(openIDAttribute.getValues(), ""));
            }

            if (openIDAttribute.getName().equals("email")) {
                user.setEmail(StringUtils.join(openIDAttribute.getValues(), ""));
            }

            if (openIDAttribute.getName().equals("lastName")) {
                user.setLastname(StringUtils.join(openIDAttribute.getValues(), ""));
            }

            if (openIDAttribute.getName().equals("language")) {
                String langage = StringUtils.join(openIDAttribute.getValues(), "");
                user.setLanguage(languageService.getSupportedLanguage(new Locale(langage)));
            }
            if (openIDAttribute.getName().equals("country")) {
                String country = StringUtils.join(openIDAttribute.getValues(), "");
                user.setCountry(countryService.getByIso2(country));
            }
        }
        if(user.getCountry() == null){
            user.setCountry(countryService.getByIso2(user.getLanguage().getCountry()));
            //user.setLanguage(languageService.getSupportedLanguage(new Locale(user.getLocale())));
        }
        user.setEmail(user.getEmail());
        user.setSocialType(SocialType.GOOGLE);
        user.setSocialUID(token.getIdentityUrl());
        return user;
    }

}
