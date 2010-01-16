package it.av.youeat.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.CountryService;
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.RistoranteService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ActivityRistoranteServiceTest {
    @Autowired
    @Qualifier("activityRistoranteService")
    private ActivityRistoranteService activityRistoranteService;
    @Autowired
    @Qualifier("userService")
    private EaterService userService;
    @Autowired
    @Qualifier("userRelationService")
    private EaterRelationService userRelationService;
    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private EaterProfileService eaterProfileService;
    private Eater user;
    private Eater userFriend;
    private Ristorante risto;
    private Country nocountry;

    @Before
    @Transactional
    public void setUp() {
        EaterProfile eaterProfile = new EaterProfile();
        eaterProfile.setName("ProfileTest");
        eaterProfileService.save(eaterProfile);

        nocountry = new Country("xx", "xxx", "test country");
        countryService.save(nocountry);

        user = new Eater();
        user.setFirstname("Alessandro");
        user.setLastname("Vincelli");
        user.setPassword("secret");
        user.setEmail("a.test@test.test");
        user.setCountry(nocountry);
        user.setUserProfile(eaterProfile);
        user = userService.addRegolarUser(user);
        assertNotNull("user is null", user);

        userFriend = new Eater();
        userFriend.setFirstname("Mario");
        userFriend.setLastname("Giansanti");
        userFriend.setPassword("secret");
        userFriend.setEmail("userfriend.test@test.test");
        userFriend.setCountry(nocountry);
        userFriend.setUserProfile(eaterProfile);
        userFriend = userService.addRegolarUser(userFriend);
        assertNotNull("userFriend is null", userFriend);

        risto = new Ristorante();
        risto.setName("RistoAlessandro");

        risto = ristoranteService.insert(risto, user);
        risto = ristoranteService.update(risto, userFriend);

        userRelationService.addFollowUser(user, userFriend);
        Collection<EaterRelation> friends = userRelationService.getAllFollowUsers(user);
        assertTrue("problem on relations", friends.size() == 1);
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
        // userService.remove(user);
        activityRistoranteService.remove(activity);

        // Using the service
        List<ActivityRistorante> activities = activityRistoranteService.findByRistorante(risto);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);

        activities = activityRistoranteService.findByUser(user);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);

        activities = activityRistoranteService.findByUser(userFriend);
        assertNotNull("Activity is null", activities);
        assertTrue(activities.size() > 0);

        /*activities = activityRistoranteService.getAll();
        for (ActivityRistorante activityRistorante : activities) {
            activityRistoranteService.remove(activityRistorante);
        }*/

    }

    @After
    @Transactional
    public void tearDown() {
        System.out.println("teardown start");
        List<EaterRelation> friends = new ArrayList<EaterRelation>(userRelationService.getAllFollowUsers(user));
        for (EaterRelation userRelation : friends) {
            userRelationService.remove(userRelation);
        }
        ristoranteService.remove(risto);
        userService.remove(user);
        userService.remove(userFriend);
        System.out.println("teardown finish");
    }

}