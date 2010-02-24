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
package it.av.youeat.repo.util;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.LanguageService;
import it.av.youeat.web.Locales;

import javax.annotation.Resource;

/**
 * Check the base SC repository structure and eventually create the base nodes
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public class RepositoryInitStructureImpl implements RepositoryInitStructure {

    @Resource(name="eaterProfileService")
    private EaterProfileService userProfileService;
    @Resource(name="eaterService")
    private EaterService userService;
    @Resource(name="languageService")
    private LanguageService languageService;


    @Override
    public void checkBaseData() throws YoueatException {
        if(languageService.getAll().size() == 0){
            Language it = new Language(Locales.ITALIAN.getLanguage(), Locales.ITALIAN.getCountry());
            Language en = new Language(Locales.ENGLISH.getLanguage(), Locales.ENGLISH.getCountry());
            Language nl = new Language(Locales.DUTCH.getDisplayLanguage(), Locales.DUTCH.getCountry());
            languageService.save(it);
            languageService.save(en);
            languageService.save(nl);
        }
        if (userProfileService.getAll().size() == 0){
            EaterProfile profileADMIN = new EaterProfile();
            profileADMIN.setName("ADMIN");
            profileADMIN.setDescription("Administration role");
            EaterProfile profileUSER = new EaterProfile();
            profileUSER.setName("USER");
            profileUSER.setDescription("Regular user role");
            profileADMIN = userProfileService.save(profileADMIN);
            profileUSER = userProfileService.save(profileUSER);
            
            Eater userADMIN = new Eater();
            userADMIN.setEmail("a.vincelli@gmail.com");
            userADMIN.setFirstname("Alessandro");
            userADMIN.setLastname("Vincelli");
            userADMIN.setPassword("admin");
            userADMIN.setUserProfile(profileADMIN);
            
            Eater userUSER = new Eater();
            userUSER.setEmail("user@user.demo");
            userUSER.setFirstname("Eater FN");
            userUSER.setLastname("Eater LN");
            userUSER.setPassword("user");
            userUSER.setUserProfile(profileUSER);
            
            Eater userAUSER = new Eater();
            userAUSER.setEmail("a@user.demo");
            userAUSER.setFirstname("Eater FA");
            userAUSER.setLastname("Eater LA");
            userAUSER.setPassword("user");
            userAUSER.setUserProfile(profileUSER);
            
            
            Eater userBUSER = new Eater();
            userBUSER.setEmail("b@user.demo");
            userBUSER.setFirstname("Eater FB");
            userBUSER.setLastname("Eater LB");
            userBUSER.setPassword("user");
            userBUSER.setUserProfile(profileUSER);
            
            Ristorante risto = new Ristorante();
            risto.setName("Default Risto");
            risto.setAddress("via roma");
            //risto.setCity("Terni");
            //risto.setCountry("Italy");
            risto.setDescription("very short descrption");
            risto.setPostalCode("05100");
            risto.setProvince("TR");
            
            userService.add(userADMIN);
            //eaterService.addRegolarUser(userAUSER);
            //eaterService.addRegolarUser(userBUSER);
            //eaterService.addRegolarUser(userUSER);
            //ristoranteService.insert(risto, userAUSER);
        }
        
    }
}
