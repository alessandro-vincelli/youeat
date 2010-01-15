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
package it.av.eatt.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.EaterProfile;
import it.av.eatt.ocm.model.data.Country;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class UserServiceTest {

    @Autowired
    @Qualifier("userService")
    private EaterService userService;
    @Autowired
    private EaterProfileService userProfileService;
    @Autowired
    private CountryService countryService;
    private EaterProfile profile;
    private Country nocountry;

    @Before
    public void setUp() throws YoueatException {
        profile = new EaterProfile();
        profile.setName("testProfile");
        profile = userProfileService.save(profile);
        nocountry = new Country("xx", "xxx", "test country");
        countryService.save(nocountry);
    }

    @After
    public void tearDown() throws YoueatException {
        userProfileService.remove(profile);
    }

    @Test
    public void testUsersBasic() throws YoueatException {

        // Basic Test
        Eater a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(profile);
        a.setCountry(nocountry);

        userService.add(a);

        // a = userService.getByPath(a.getPath());
        assertNotNull("A is null", a);
        assertNotNull("Profile is null", a.getUserProfile());
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());

        Collection<Eater> all = userService.getAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);

        a.setLastname("Modified");
        userService.update(a);
        assertEquals("Invalid value for test", "Modified", a.getLastname());
        a = userService.getByEmail("userServiceTest@test");
        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());

        /*List<Eater> found = userService.freeTextSearch("Ale*");
        assertNotNull(found);
        assertTrue(found.size() > 0);
        
        found = userService.freeTextSearch("vinc*");
        assertNotNull(found);
        assertTrue(found.size() > 0);
        */

        userService.remove(a);

        // Test with Friend
        Eater b = new Eater();
        b.setFirstname("Alessandro");
        b.setLastname("Vincelli");
        b.setPassword("secret");
        b.setEmail("userServiceTest@test.com");
        b.setUserProfile(profile);
        b.setCountry(nocountry);
        b = userService.add(b);
        assertNotNull("A is null", b);

        Eater c = new Eater();
        c.setFirstname("Arnaldo");
        c.setLastname("Vincelli");
        c.setPassword("secret");
        c.setEmail("userServiceTest@test2.com");
        c.setCountry(nocountry);
        c.setUserProfile(profile);
        c = userService.addRegolarUser(c);
        assertNotNull("C is null", c);

        // EaterRelation relations = UserRelationImpl.createFriendRelation(c, a);
        // relations.setPath("/relPath");
        // jcrMappingTemplate.insert(relations);
        // ArrayList<EaterRelation> userRels = new ArrayList<EaterRelation>(1);
        // userRels.add(relations);
        // c.setUserRelation(userRels);
        // c = userService.update(c);
        // assertNotNull("Friends is null", c.getUserRelation().get(0));
        // assertNotNull("Friend C is null", c.getUserRelation().get(0).getFromUser());
        // assertNotNull("Friend A null", c.getUserRelation().get(0).getToUser());
        // assertEquals(a.getEmail(), c.getUserRelation().get(0).getToUser().getEmail());

        userService.remove(c);
        userService.remove(b);
        // userService.remove(userService.getByPath(aPath));

    }

}