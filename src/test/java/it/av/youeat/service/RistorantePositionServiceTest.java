package it.av.youeat.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.geo.Location;
import it.av.youeat.ocm.model.geo.RistorantePosition;
import it.av.youeat.ocm.model.geo.RistorantePositionAndDistance;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:test-application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class RistorantePositionServiceTest extends YoueatTest {

    @Autowired
    @Qualifier("eaterService")
    private EaterService userService;
    @Autowired
    private RistorantePositionService ristorantePositionService;
    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private ActivityRistoranteService activityRistoranteService;
    private Eater user;
    private Ristorante mammalina;
    private Ristorante ciacco;
    private Ristorante mola;
    private Ristorante mora;

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

        mammalina = new Ristorante();
        mammalina.setName("Mammalina");
        mammalina.setCity(getNocity());
        mammalina.setCountry(getNocountry());
        mammalina = ristoranteService.insert(mammalina, user);

        ciacco = new Ristorante();
        ciacco.setName("Ciacco");
        ciacco.setCity(getNocity());
        ciacco.setCountry(getNocountry());
        ciacco = ristoranteService.insert(ciacco, user);

        mola = new Ristorante();
        mola.setName("Mola");
        mola.setCity(getNocity());
        mola.setCountry(getNocountry());
        mola = ristoranteService.insert(mola, user);

        mora = new Ristorante();
        mora.setName("Mora");
        mora.setCity(getNocity());
        mora.setCountry(getNocountry());
        mora = ristoranteService.insert(mora, user);
    }

    @Test
    @Transactional
    public void testBasicOperationsOnPosition() {

        Location where = new Location(Double.valueOf("42.5625543"), Double.valueOf("12.6506796"));
        RistorantePosition position = new RistorantePosition(mammalina, where);
        position = ristorantePositionService.save(position);
        assertNotNull(position);
        assertNotNull(position.getId());
        assertNotNull(position.getRistorante());
        assertEquals(mammalina, position.getRistorante());
        assertEquals(where.getLatitude(), position.getWhere().getLatitude());
        assertEquals(where.getLongitude(), position.getWhere().getLongitude());

        position = ristorantePositionService.getByRistorante(mammalina);
        assertNotNull(position);
        
        ristorantePositionService.remove(position);
        position = ristorantePositionService.getByRistorante(mammalina);
        assertNull(position);

    }

    @Test
    @Transactional
    public void testAroundOperation() {
        Location whereMammalina = new Location(Double.valueOf("42.5625543"), Double.valueOf("12.6506796"));
        Location whereCiacco = new Location(Double.valueOf("42.5631037"), Double.valueOf("12.6440223"));
        Location whereMola = new Location(Double.valueOf("42.626429"), Double.valueOf("12.8284772"));
        Location whereMora = new Location(Double.valueOf("42.5582722"), Double.valueOf("12.6386542"));

        RistorantePosition positionMammalina = new RistorantePosition(mammalina, whereMammalina);
        RistorantePosition positionCiacco = new RistorantePosition(ciacco, whereCiacco);
        RistorantePosition positionMola = new RistorantePosition(mola, whereMola);
        RistorantePosition positionMora = new RistorantePosition(mora, whereMora);

        positionMammalina = ristorantePositionService.save(positionMammalina);
        positionCiacco = ristorantePositionService.save(positionCiacco);
        positionMola = ristorantePositionService.save(positionMola);
        positionMora = ristorantePositionService.save(positionMora);

        List<RistorantePositionAndDistance> results = ristorantePositionService.around(whereMammalina, 600, 10);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(mammalina, results.get(0).getRistorante());
        assertEquals(ciacco, results.get(1).getRistorante());

        results = ristorantePositionService.around(whereMammalina, 1600, 10);
        assertNotNull(results);
        assertTrue(results.size() == 3);
        assertEquals(mammalina, results.get(0).getRistorante());
        assertEquals(ciacco, results.get(1).getRistorante());
        assertEquals(mora, results.get(2).getRistorante());

        results = ristorantePositionService.around(whereMammalina, 160000, 10);
        assertNotNull(results);
        assertEquals(4, results.size());
        assertEquals(mammalina, results.get(0).getRistorante());
        assertEquals(ciacco, results.get(1).getRistorante());
        assertEquals(mora, results.get(2).getRistorante());
        assertEquals(mola, results.get(3).getRistorante());

        RistorantePosition positionCiaccoFromDB = results.get(1).getRistorantePosition();
        Long distanceCalculated = (long)positionCiaccoFromDB.getWhere().distanceFrom(whereMammalina).doubleValue();

        assertEquals(distanceCalculated, results.get(1).getDistanceInMeters());
        
        //position on favorites ristos
        activityRistoranteService.addRistoAsFavorite(user, ciacco);
        List<RistorantePositionAndDistance> favoritesRisto = ristorantePositionService.favourites(user, whereMammalina, 2);
        assertEquals(1, favoritesRisto.size());
        distanceCalculated = (long)positionCiaccoFromDB.getWhere().distanceFrom(whereMammalina).doubleValue();
        assertEquals(distanceCalculated, results.get(1).getDistanceInMeters());
        
        activityRistoranteService.addRistoAsFavorite(user, mola);
        favoritesRisto = ristorantePositionService.favourites(user, whereMammalina, 1);
        assertEquals(1, favoritesRisto.size());
        assertEquals(ciacco, results.get(1).getRistorante());
        
        favoritesRisto = ristorantePositionService.favourites(user, whereMammalina, 2);
        assertEquals(2, favoritesRisto.size());
        assertEquals(ciacco, favoritesRisto.get(0).getRistorante());
        assertEquals(mola, favoritesRisto.get(1).getRistorante());
        

    }

 }