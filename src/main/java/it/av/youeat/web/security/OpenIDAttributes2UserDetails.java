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

import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * Utility class to wotk with OpenID
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface OpenIDAttributes2UserDetails {

    /**
     * Extracts OpenID attributes and use it to create an instance of an Eater
     * 
     * @param token
     * @return eater populated with the found openID attributes
     */
    public Eater extract(OpenIDAuthenticationToken token);

}