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
public class EaterRelationServiceTest extends YoueatTest {

    @Autowired
    @Qualifier("eaterService")
    private EaterService userService;
    @Autowired
    private EaterRelationService userRelationService;
    @Autowired
    private ActivityRelationService activityRelationService;

    private Eater a;
    private Eater b;
    private Eater admin;

    @Before
    public void setUp() {
        super.setUp();
        a = new Eater();
        a.setFirstname("Alessandro");
        a.setLastname("Vincelli");
        a.setPassword("secret");
        a.setEmail("a.userRelationService@test.com");
        a.setCountry(getNocountry());
        a.setLanguage(getLanguage());
        a = userService.addRegolarUser(a);
        assertNotNull("A is null", a);

        b = new Eater();
        b.setFirstname("Mario");
        b.setLastname("Vincelli");
        b.setPassword("secret");
        b.setEmail("m.userRelationService@test2.com");
        b.setCountry(getNocountry());
        b.setLanguage(getLanguage());
        b = userService.addRegolarUser(b);
        assertNotNull("B is null", b);

        admin = new Eater();
        admin.setFirstname("admin");
        admin.setLastname("admin");
        admin.setPassword("admin");
        admin.setEmail("admin@test2.com");
        admin.setCountry(getNocountry());
        admin.setLanguage(getLanguage());
        admin = userService.addAdminUser(admin);
        assertNotNull("admin is null", admin);
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
        EaterRelation friendRel = userRelationService.getRelation(a, b);
        assertNotNull("user relation non present", friendRel);
        assertTrue("user relation status not correct", !friendRel.isActiveFriendRelation());
        assertTrue("user relation status not correct", friendRel.isFollowingRelation());

        userRelationService.remove(relationFollow);
        friends = userRelationService.getAllRelations(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 0);

        EaterRelation relation = userRelationService.addFriendRequest(a, b);
        assertNotNull(relation);

        friends = userRelationService.getAllRelations(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);

        friendRel = userRelationService.getRelation(a, b);
        assertNotNull("user relation non present", friendRel);
        assertTrue("user friend status not correct", !friendRel.isActiveFriendRelation());
        assertTrue("user friend status not correct", !friendRel.isFollowingRelation());

        userRelationService.performFriendRequestIgnore(relation);
        friends = userRelationService.getFriends(a);
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
        friendRel = userRelationService.getRelation(a, b);
        assertNotNull("user relation non present", friendRel);
        assertTrue("user friend status not correct", !friendRel.isActiveFriendRelation());
        assertTrue("user friend status not correct", !friendRel.isFollowingRelation());

        userRelationService.remove(relation);

        relation = userRelationService.addFriendRequest(a, b);
        assertNotNull(relation);
        List<EaterRelation> friendRelPending = userRelationService.getAllPendingFriendRequetToUsers(b);
        assertTrue(friendRelPending.size() == 1);

        userRelationService.performFriendRequestConfirm(relation);
        friends = userRelationService.getFriends(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 1);

        friendRelPending = userRelationService.getAllPendingFriendRequetToUsers(b);
        assertTrue(friendRelPending.size() == 0);

        try {
            userRelationService.performFriendRequestConfirm(relation);
        } catch (Exception e) {
            // expected
        }

        friendRel = userRelationService.getRelation(a, b);
        assertNotNull("user relation non present", friendRel);
        assertTrue("user friend status not correct", friendRel.isActiveFriendRelation());
        assertTrue("user friend status not correct", !friendRel.isFollowingRelation());

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
        friends = userRelationService.getFriends(a);
        assertNotNull(friends);
        assertTrue(friends.size() == 0);

        Collection<Eater> relatedUser = userService.findUserWithoutRelation(a);
        assertNotNull(relatedUser);
        assertTrue(relatedUser.size() == 0);

        relatedUser = userService.findUserWithoutRelation(a, "Mario");
        assertNotNull(relatedUser);
        assertTrue(relatedUser.size() == 0);
    }

    @Test
    public void getCommonFriendsTest() {

        Eater c = new Eater();
        c.setFirstname("Filippo");
        c.setLastname("Vincelli");
        c.setPassword("secret");
        c.setEmail("m.userRelationService@test3.com");
        c.setCountry(getNocountry());
        c.setLanguage(getLanguage());
        c = userService.addRegolarUser(c);
        assertNotNull("C is null", c);

        List<Eater> result = userRelationService.getCommonFriends(b, c);
        assertTrue(result.isEmpty());
        int number = userRelationService.countCommonFriends(b, c);
        assertEquals(0, number);

        EaterRelation relA2B = userRelationService.addFriendRequest(a, b);
        number = userRelationService.countCommonFriends(a, b);
        assertEquals(0, number);

        userRelationService.performFriendRequestConfirm(relA2B);

        result = userRelationService.getCommonFriends(b, c);
        assertTrue(result.size() == 0);
        number = userRelationService.countCommonFriends(b, c);
        assertEquals(0, number);

        EaterRelation relA2C = userRelationService.addFriendRequest(a, c);
        userRelationService.performFriendRequestConfirm(relA2C);

        result = userRelationService.getCommonFriends(b, c);
        assertTrue(result.size() == 1);
        number = userRelationService.countCommonFriends(b, c);
        assertEquals(1, number);

    }

    @Test
    public void countFriends() {

        Eater c = new Eater();
        c.setFirstname("Filippo");
        c.setLastname("Vincelli");
        c.setPassword("secret");
        c.setEmail("m.userRelationService@test3.com");
        c.setCountry(getNocountry());
        c.setLanguage(getLanguage());
        c = userService.addRegolarUser(c);
        assertNotNull("C is null", c);

        int number = userRelationService.countFriends(b);
        assertEquals(0, number);

        EaterRelation relA2B = userRelationService.addFriendRequest(a, b);
        number = userRelationService.countFriends(b);
        assertEquals(0, number);
        number = userRelationService.countFriends(a);
        assertEquals(0, number);

        userRelationService.performFriendRequestConfirm(relA2B);

        number = userRelationService.countFriends(b);
        assertEquals(1, number);
        number = userRelationService.countFriends(b);
        assertEquals(1, number);

        EaterRelation relA2C = userRelationService.addFriendRequest(a, c);
        number = userRelationService.countFriends(a);
        assertEquals(1, number);
        number = userRelationService.countFriends(c);
        assertEquals(0, number);

        userRelationService.performFriendRequestConfirm(relA2C);
        number = userRelationService.countFriends(a);
        assertEquals(2, number);
        number = userRelationService.countFriends(c);
        assertEquals(1, number);
        number = userRelationService.countFriends(b);
        assertEquals(1, number);

    }

    @Test
    public void getNonCommonFriendsTest() {

        Eater c = new Eater();
        c.setFirstname("Filippo");
        c.setLastname("Vincelli");
        c.setPassword("secret");
        c.setEmail("m.userRelationService@test3.com");
        c.setCountry(getNocountry());
        c.setLanguage(getLanguage());
        c = userService.addRegolarUser(c);
        assertNotNull("C is null", c);

        List<Eater> result = userRelationService.getNonCommonFriends(b, c);
        assertTrue(result.isEmpty());
        // friend request A-B
        EaterRelation relA2B = userRelationService.addFriendRequest(a, b);

        result = userRelationService.getNonCommonFriends(b, c);
        assertTrue(result.isEmpty());
        result = userRelationService.getNonCommonFriends(b, a);
        assertTrue(result.isEmpty());
        // friend confirm A-B
        userRelationService.performFriendRequestConfirm(relA2B);

        result = userRelationService.getNonCommonFriends(b, c);
        assertEquals(1, result.size());
        result = userRelationService.getNonCommonFriends(c, b);
        assertEquals(0, result.size());

        result = userRelationService.getNonCommonFriends(a, c);
        assertEquals(1, result.size());
        result = userRelationService.getNonCommonFriends(c, a);
        assertEquals(0, result.size());

        EaterRelation relA2C = userRelationService.addFriendRequest(a, c);
        userRelationService.performFriendRequestConfirm(relA2C);

        result = userRelationService.getNonCommonFriends(a, c);
        assertEquals(1, result.size());
        assertEquals(b, result.get(0));
        result = userRelationService.getNonCommonFriends(c, a);
        assertEquals(0, result.size());
        result = userRelationService.getNonCommonFriends(b, a);
        assertEquals(0, result.size());
        result = userRelationService.getNonCommonFriends(a, b);
        assertEquals(1, result.size());
        assertEquals(c, result.get(0));

    }

}