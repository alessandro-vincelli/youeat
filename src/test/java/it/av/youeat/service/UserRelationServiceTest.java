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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;

import java.util.Collection;
import java.util.List;

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
public class UserRelationServiceTest extends YoueatTest {

    @Autowired
    @Qualifier("eaterService")
    private EaterService userService;
    @Autowired
    private EaterRelationService userRelationService;
    @Autowired
    private ActivityRelationService activityRelationService;

    private Eater a;
    private Eater b;

    @Before
    public void setUp() {
        super.setUp();
        a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("a.userRelationService@test.com");
        a.setCountry(getNocountry());
        a = userService.addRegolarUser(a);
        assertNotNull("A is null", a);

        b = new Eater();
        b.setFirstname("Mario");
        b.setLastname("Vincelli");
        b.setPassword("secret");
        b.setEmail("m.userRelationService@test2.com");
        b.setCountry(getNocountry());
        b = userService.addRegolarUser(b);
        assertNotNull("B is null", b);
    }

    @After
    public void tearDown() throws YoueatException {
        userService.remove(a);
        userService.remove(b);
    }

    @Test
    public void testUserWithFriend() throws YoueatException {

        EaterRelation relationFollow = userRelationService.addFollowUser(a, b);
        assertNotNull(relationFollow);
        assertNotNull(relationFollow.getFromUser().getEmail());
        assertNotNull(relationFollow.getToUser().getEmail());

        // refresh the user a
        a = userService.getByEmail(a.getEmail());
        // fix the lazy load
        // assertNotNull(a.getUserRelation());
        // assertTrue(a.getUserRelation().get(0).getType() == EaterRelation.TYPE_FOLLOW);

        Collection<EaterRelation> friends = userRelationService.getAllFollowUsers(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);

        friends = userRelationService.getAllRelations(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);

        List<ActivityEaterRelation> activities = activityRelationService.findByEater(a);
        assertTrue(activities.size() == 1);
        assertTrue(activities.get(0).getEaterActivityType().equals(ActivityEaterRelation.TYPE_STARTS_FOLLOWING));

        userRelationService.remove(relationFollow);
        friends = userRelationService.getAllRelations(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 0);

        EaterRelation relation = userRelationService.addFriendRequest(a, b);
        assertNotNull(relation);

        friends = userRelationService.getAllRelations(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);

        userRelationService.performFriendRequestIgnore(relation);
        friends = userRelationService.getAllFriendUsers(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 0);
        friends = userRelationService.getAllRelations(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);
        try {
            userRelationService.performFriendRequestIgnore(relation);
        } catch (Exception e) {
            // expected
        }
        userRelationService.remove(relation);

        relation = userRelationService.addFriendRequest(a, b);
        assertNotNull(relation);

        userRelationService.performFriendRequestConfirm(relation);
        friends = userRelationService.getAllFriendUsers(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);
        try {
            userRelationService.performFriendRequestConfirm(relation);
        } catch (Exception e) {
            // expected
        }

        activities = activityRelationService.findByEater(a);
        assertTrue(activities.size() == 2);
        assertTrue(activities.get(0).getEaterActivityType().equals(ActivityEaterRelation.TYPE_ARE_FRIENDS));

        activities = activityRelationService.findByEater(b);
        assertTrue(activities.size() == 2);
        assertTrue(activities.get(0).getEaterActivityType().equals(ActivityEaterRelation.TYPE_ARE_FRIENDS));

        activities = activityRelationService.findByEaterFriend(a);
        assertTrue(activities.size() == 2);
        assertTrue(activities.get(0).getEaterActivityType().equals(ActivityEaterRelation.TYPE_ARE_FRIENDS));

        userRelationService.remove(relation);
        friends = userRelationService.getAllFriendUsers(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 0);

        Collection<Eater> relatedUser = userService.findUserWithoutRelation(a);
        assertNotNull(relatedUser);
        assertTrue(relatedUser.size() == 0);

        relatedUser = userService.findUserWithoutRelation(a, "Mario");
        assertNotNull(relatedUser);
        assertTrue(relatedUser.size() == 0);
    }
}