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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Comment;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.SocialType;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class EaterServiceTest extends YoueatTest {

    @Autowired
    private EaterService userService;
    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EaterRelationService eaterRelationService;
    @Autowired
    private DialogService dialogService;
    private static String socialUid = "1x1x1x1";
    
    @Before
    @Transactional
    public void setUp() {
        super.setUp();
    }

    @After
    @Transactional
    public void tearDown() {
    }


    @Test
    public void testEaterService_generic() throws YoueatException {

        // Basic Test
        Eater a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(getProfile());
        a.setCountry(getNocountry());
        a.setLanguage(getLanguage());
        a.setSocialType(SocialType.FACEBOOK);
        a.setSocialUID(socialUid);

        userService.add(a);

        // a = eaterService.getByPath(a.getPath());
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

        a = userService.getBySocialUID(socialUid, SocialType.FACEBOOK);
        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());

        /*List<Eater> found = eaterService.freeTextSearch("Ale*");
        assertNotNull(found);
        assertTrue(found.size() > 0);
        
        found = eaterService.freeTextSearch("vinc*");
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
        b.setUserProfile(getProfile());
        b.setCountry(getNocountry());
        b.setLanguage(getLanguage());
        b = userService.add(b);
        assertNotNull("B is null", b);

        Eater c = new Eater();
        c.setFirstname("Arnaldo");
        c.setLastname("Vincelli");
        c.setPassword("secret");
        c.setEmail("userServiceTest@test2.com");
        c.setCountry(getNocountry());
        c.setLanguage(getLanguage());
        c = userService.addRegolarUser(c);

        assertNotNull("C is null", c);

        // EaterRelation relations = UserRelationImpl.createFriendRelation(c, a);
        // relations.setPath("/relPath");
        // jcrMappingTemplate.insert(relations);
        // ArrayList<EaterRelation> userRels = new ArrayList<EaterRelation>(1);
        // userRels.add(relations);
        // c.setUserRelation(userRels);
        // c = eaterService.update(c);
        // assertNotNull("Friends is null", c.getUserRelation().get(0));
        // assertNotNull("Friend C is null", c.getUserRelation().get(0).getFromUser());
        // assertNotNull("Friend A null", c.getUserRelation().get(0).getToUser());
        // assertEquals(a.getEmail(), c.getUserRelation().get(0).getToUser().getEmail());
        
        List<Eater> sortedResults = userService.find("vincelli", 0, 1, Eater.FIRSTNAME, true);
        assertEquals(1, sortedResults.size());
        assertEquals(b, sortedResults.get(0));
        
        sortedResults = userService.find("vincelli", 0, 2, Eater.FIRSTNAME, true);
        assertEquals(2, sortedResults.size());
        assertEquals(b, sortedResults.get(0));
        assertEquals(c, sortedResults.get(1));
        
        sortedResults = userService.find("vincelli", 0, 2, Eater.FIRSTNAME, false);
        assertEquals(2, sortedResults.size());
        assertEquals(c, sortedResults.get(0));
        assertEquals(b, sortedResults.get(1));
        
        sortedResults = userService.find("vincelli", 0, 2, Eater.CREATIONTIME, false);
        assertEquals(2, sortedResults.size());
        assertEquals(c, sortedResults.get(0));
        assertEquals(b, sortedResults.get(1));
        
        sortedResults = userService.find("vincelli", 0, 2, Eater.CREATIONTIME, true);
        assertEquals(2, sortedResults.size());
        assertEquals(b, sortedResults.get(0));
        assertEquals(c, sortedResults.get(1));
        
        sortedResults = userService.find(null, 0, 2, Eater.CREATIONTIME, true);
        assertEquals(2, sortedResults.size());
        
        sortedResults = userService.find("", 0, 2, Eater.CREATIONTIME, true);
        assertEquals(2, sortedResults.size());
        
        int countEater = userService.count();
        assertEquals(2, countEater);
        
        countEater = userService.count("vincelli");
        assertEquals(2, countEater);
        
        countEater = userService.count(null);
        assertEquals(2, countEater);

        userService.remove(c);
        userService.remove(b);
        // eaterService.remove(eaterService.getByPath(aPath));

    }

    @Test
    public void testEaterService_FacebookUser() {

        // Basic Test
        Eater a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setCountry(getNocountry());
        a.setLanguage(getLanguage());
        a.setSocialUID(socialUid);

        userService.addFacebookUser(a);

        // a = eaterService.getByPath(a.getPath());
        assertNotNull("A is null", a);
        assertNotNull("Profile is null", a.getUserProfile());
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());

        a = userService.getBySocialUID(socialUid, SocialType.FACEBOOK);
        assertNotNull("A is null", a);
        assertEquals("Invalid value for test", "Alessandro", a.getFirstname());
        userService.remove(a);
    }
    
    @Test
    public void testEaterService_remove() throws YoueatException {

        Eater a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("userServiceTest@test");
        a.setUserProfile(getProfile());
        a.setCountry(getNocountry());
        a.setLanguage(getLanguage());

        userService.add(a);
        
        Eater b = new Eater();
        b.setFirstname("Alessandro");
        b.setLastname("Vincelli");
        b.setPassword("secret");
        b.setEmail("userServiceTest@test.com");
        b.setUserProfile(getProfile());
        b.setCountry(getNocountry());
        b.setLanguage(getLanguage());
        b = userService.add(b);
        assertNotNull("B is null", b);

        userService.add(b);
        
        EaterRelation relation = eaterRelationService.addFriendRequest(a, b);
        eaterRelationService.performFriendRequestConfirm(relation);
        dialogService.startNewDialog(a, b, new Message("title", "body"));
        
        Ristorante rist = new Ristorante();
        rist.setName("RistoTest");
        rist.setCity(getNocity());
        rist.setCountry(getNocountry());
        rist = ristoranteService.insert(rist, a);

        commentService.save(new Comment("", "body", a));
        
        userService.remove(a);
        userService.remove(b);
        
    }
}
