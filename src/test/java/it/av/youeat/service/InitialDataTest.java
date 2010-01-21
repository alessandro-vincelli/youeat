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

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.data.Country;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class InitialDataTest extends YoueatTest {

    @Autowired
    private EaterService userService;
    @Autowired
    private EaterProfileService eaterProfileService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private LanguageService languageService;
    
    @Transactional
    @Test
    public void testInitialData(){
        if(userService.getByEmail("a.vincelli@gmail.com") == null){
            EaterProfile adminProfile = eaterProfileService.getByName(EaterProfile.ADMIN);
            Country italia = countryService.getAll().get(0);
            Eater a = new Eater();
            a.setFirstname("Alessandro");
            a.setLastname("Vincelli");
            a.setPassword("14asd03a");
            a.setEmail("a.vincelli@gmail.com");
            a.setUserProfile(adminProfile);
            a.setCountry(italia);
            a.setLanguage(languageService.getAll().get(0));
            userService.add(a);
        }
    }

}