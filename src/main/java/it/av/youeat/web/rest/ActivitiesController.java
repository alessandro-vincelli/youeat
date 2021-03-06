package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.util.PeriodUtil;
import it.av.youeat.web.security.SecurityContextHelper;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * Provides some REST services for restaurants activities. Creates JSON objects.
 * 
 * @author Alessandro Vincelli
 */
@Controller
public class ActivitiesController {

    private ActivityRistoranteService activityRistoranteService;
    private RistoranteService ristoranteService;
    private PeriodUtil periodUtil;
    private MessageSource messageSource;
    private MappingJacksonJsonView jsonView;

    /**
     * Constructor
     * 
     * @param activityRistoranteService (not null)
     * @param periodUtil (not null)
     * @param messageSource (not null)
     * @param jsonView (not null)
     * @param ristoranteService (not null)
     */
    @Autowired
    public ActivitiesController(ActivityRistoranteService activityRistoranteService, PeriodUtil periodUtil,
            MessageSource messageSource, MappingJacksonJsonView jsonView, RistoranteService ristoranteService) {
        Assert.notNull(activityRistoranteService);
        Assert.notNull(periodUtil);
        Assert.notNull(messageSource);
        Assert.notNull(jsonView);
        Assert.notNull(ristoranteService);
        this.activityRistoranteService = activityRistoranteService;
        this.periodUtil = periodUtil;
        this.messageSource = messageSource;
        this.jsonView = jsonView;
        this.ristoranteService = ristoranteService;
    }

    /**
     * Returns the activities of the logged user
     * 
     * @param model
     * @return a list of activities
     */
    @RequestMapping(value = "/security/friendActivitiesByLoggedUser")
    @Secured(EaterProfile.USER)
    public ModelAndView getFriendActivitiesByLoggedUser(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        List<ActivityRistorante> activityRistorantes = activityRistoranteService.findByUserFriendAndUser(SecurityContextHelper
                .getAuthenticatedUser(), 0, 30);
        Locale locale = new Locale(SecurityContextHelper.getAuthenticatedUser().getLanguage().getLanguage());
        setElapsedTimeAndDesc(activityRistorantes, locale);
        modelAndView.addObject(activityRistorantes);
        return modelAndView;
    }

    /**
     * Returns the last activities on restaurants
     * 
     * @param model
     * @return a list of activities
     */
    @RequestMapping(value = "/security/lastActivitiesOnRestaurants")
    @Secured(EaterProfile.USER)
    public ModelAndView getLastActivitiesOnRestaurants(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        List<ActivityRistorante> activityRistorantes = activityRistoranteService.getLasts(30);
        Locale locale = new Locale(SecurityContextHelper.getAuthenticatedUser().getLanguage().getLanguage());
        setElapsedTimeAndDesc(activityRistorantes, locale);
        modelAndView.addObject(activityRistorantes);
        return modelAndView;
    }
    
    /**
     * Returns the last activities on the given restaurant
     * 
     * @param model
     * @param ristoId the id of the risto
     * @return a list of activities
     */
    @RequestMapping(value = "/security/lastActivitiesOnRestaurant/{ristoId}")
    @Secured(EaterProfile.USER)
    public ModelAndView getLastActivitiesOnRestaurant(@PathVariable String ristoId, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        List<ActivityRistorante> activityRistorantes = activityRistoranteService.findByRistoranteId(ristoId);
        Locale locale = new Locale(SecurityContextHelper.getAuthenticatedUser().getLanguage().getLanguage());
        setElapsedTimeAndDesc(activityRistorantes, locale);
        modelAndView.addObject(activityRistorantes);
        return modelAndView;
    }

    /**
     * Set the elapsed time and the description of the activity
     * 
     * @param activityRistorantes
     * @param locale
     * @return list of activities
     */
    List<ActivityRistorante> setElapsedTimeAndDesc(List<ActivityRistorante> activityRistorantes, Locale locale) {
        for (ActivityRistorante activityRistorante : activityRistorantes) {
            activityRistorante.setElapsedTime(periodUtil.getPeriod(activityRistorante.getDate().getTime(), locale));
            activityRistorante.setActivityDesc(messageSource.getMessage(activityRistorante.getType(), null, locale));
        }
        return activityRistorantes;
    }
    
    /**
     * Returns the list of users that are friends of the logged user and have the given restaurants as favorite
     * 
     * @param model
     * @return a list of users
     */
    @RequestMapping(value = "/security/friendFavorite/{ristoId}")
    @Secured(EaterProfile.USER)
    public ModelAndView getFriendRistoAsFavorites(@PathVariable String ristoId, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        Ristorante risto = ristoranteService.getByID(ristoId);
        modelAndView.addObject(activityRistoranteService.findEatersHasFavoritesRistoFriendsOf(risto, SecurityContextHelper.getAuthenticatedUser(), 50));
        return modelAndView;
    }
    
    /**
     * Returns the list of users that are friends of the logged user and have tried the given restaurants
     * 
     * @param model
     * @return a list of users
     */
    @RequestMapping(value = "/security/friendTried/{ristoId}")
    @Secured(EaterProfile.USER)
    public ModelAndView getFriendTriedRisto(@PathVariable String ristoId, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        Ristorante risto = ristoranteService.getByID(ristoId);
        modelAndView.addObject(activityRistoranteService.findByFriendThatEatOnRistorante(SecurityContextHelper.getAuthenticatedUser(), risto));
        return modelAndView;
    }

}