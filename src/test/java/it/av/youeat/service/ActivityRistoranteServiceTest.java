package it.av.youeat.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.model.Ristorante;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ActivityRistoranteServiceTest extends YoueatTest{
    @Autowired
    @Qualifier("activityRistoranteService")
    private ActivityRistoranteService activityRistoranteService;
    @Autowired
    @Qualifier("eaterService")
    private EaterService userService;
    @Autowired
    @Qualifier("eaterRelationService")
    private EaterRelationService userRelationService;
    @Autowired
    private RistoranteService ristoranteService;
    private Eater user;
    private Eater userFriend;
    private Eater userNotFriend;
    private Ristorante risto;
   

    @Before
    @Transactional
    public void setUp() {
        super.setUp();
        user = new Eater();
        user.setFirstname("Alessandro");
        user.setLastname("Vincelli");
        user.setPassword("secret");
        user.setEmail("a.test@test.test");
        user.setCountry(getNocountry());
        user.setLanguage(getLanguage());
        user = userService.addRegolarUser(user);
        assertNotNull("user is null", user);

        userFriend = new Eater();
        userFriend.setFirstname("Mario");
        userFriend.setLastname("Giansanti");
        userFriend.setPassword("secret");
        userFriend.setEmail("userfriend.test@test.test");
        userFriend.setCountry(getNocountry());
        userFriend.setLanguage(getLanguage());
        userFriend = userService.addRegolarUser(userFriend);
        assertNotNull("userFriend is null", userFriend);
        
        userNotFriend = new Eater();
        userNotFriend.setFirstname("Mario");
        userNotFriend.setLastname("Bross");
        userNotFriend.setPassword("secret");
        userNotFriend.setEmail("mariobross@test.test");
        userNotFriend.setCountry(getNocountry());
        userNotFriend.setLanguage(getLanguage());
        userNotFriend = userService.addRegolarUser(userNotFriend);
        assertNotNull("userFriend is null", userNotFriend);


        risto = new Ristorante();
        risto.setName("RistoAlessandro");
        risto.setCity(getNocity());
        risto.setCountry(getNocountry());

        risto = ristoranteService.insert(risto, user);
        risto = ristoranteService.update(risto, userFriend);

        //userRelationService.addFollowUser(user, userFriend);
        EaterRelation friendRelation = userRelationService.addFriendRequest(user, userFriend);
        userRelationService.performFriendRequestConfirm(friendRelation);
        //Collection<EaterRelation> friends = userRelationService.getAllFollowUsers(user);
        //assertTrue("problem on relations", friends.size() == 1);
    }

    @Test
    @Transactional
    public void testRistoActivity() {
        // Manual
        ActivityRistorante activity = new ActivityRistorante();
        activity.setDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        activity.setEater(user);
        activity.setRistorante(risto);
        activity.setType(ActivityRistorante.TYPE_ADDED);
        activityRistoranteService.save(activity);

        assertNotNull("Activity is null", activity);
        // assertEquals("Invalid value for test", risto.getUuid(), activity.getRistorante().getUuid());
        // assertEquals("Invalid value for test", user.getEmail(), activity.getUser().getEmail());

        // Collection<RistoEditActivity> activities = activityService.findByDate(activity.getDate());
        // assertNotNull(activities);
        // assertTrue(activities.size() > 0);
        user = userService.getByEmail(user.getEmail());
        // eaterService.remove(user);
        activityRistoranteService.remove(activity);

        // Using the service
        List<ActivityRistorante> activities = activityRistoranteService.findByRistorante(risto);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);

        activities = activityRistoranteService.findByEater(user);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);

        activities = activityRistoranteService.findByEater(userFriend);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);
        
        activities = activityRistoranteService.findByUserFriendAndUser(user, 0, 10);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);
        
        activities = activityRistoranteService.findByFriendWithActivitiesOnRistorante(userFriend, risto, ActivityRistorante.TYPE_ADDED);
        assertTrue(activities.size() > 0);
        
        int countByType  = activityRistoranteService.countByRistoAndType(risto, ActivityRistorante.TYPE_ADDED);
        assertTrue(countByType > 0);
        
        activities  = activityRistoranteService.findByFriendContributionsOnRistorante(userFriend, risto);
        assertTrue(activities.size() > 0);
        
        int countContributions  = activityRistoranteService.countContributionsOnRistorante(risto);
        assertTrue(countContributions > 0);
        
        List<Ristorante> ristos = ristoranteService.freeTextSearch(risto.getName());
        for (Ristorante ristorante : ristos) {
            //System.out.println(ristorante.getName());
        }
        
        List<Ristorante> contributenRisto = activityRistoranteService.findContributedByEater(user, 0);
        assertTrue(contributenRisto.get(0).equals(risto));
        
        activityRistoranteService.addRistoAsFavourite(user, risto);
        List<Ristorante> favouritesRisto = activityRistoranteService.findFavoriteRisto(user, 1);
        assertTrue(favouritesRisto.get(0).equals(risto));
        
        activityRistoranteService.removeRistoAsFavourite(user, risto);
        favouritesRisto = activityRistoranteService.findFavoriteRisto(user, 1);
        assertTrue(favouritesRisto.isEmpty());
    }
    
    
    
    @Test
    @Transactional
    public void testRistoAndRemovingActivity() {
        // Manual
        ActivityRistorante activity = new ActivityRistorante();
        activity.setDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        activity.setEater(user);
        activity.setRistorante(risto);
        activity.setType(ActivityRistorante.TYPE_ADDED);
        activityRistoranteService.save(activity);

        assertNotNull("Activity is null", activity);
                user = userService.getByEmail(user.getEmail());
        // eaterService.remove(user);
        //activityRistoranteService.remove(activity);
        activityRistoranteService.removeByEater(user);
         
    }

    @After
    @Transactional
    public void tearDown() {
        List<EaterRelation> friends = new ArrayList<EaterRelation>(userRelationService.getAllFollowUsers(user));
        for (EaterRelation userRelation : friends) {
            userRelationService.remove(userRelation);
        }
        userService.remove(user);
        userService.remove(userFriend);
        ristoranteService.remove(risto);
    }

}