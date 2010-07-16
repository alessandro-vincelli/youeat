package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.security.SecurityContextHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * Provides some REST services and actions on restaurants. Creates JSON objects.
 * 
 * @author Alessandro Vincelli
 */
@Controller
public class ActionsOnRistorantiController {

    private RistoranteService ristoranteService;
    private ActivityRistoranteService activityRistoranteService;
    private MappingJacksonJsonView jsonView;

    /**
     * Constructor
     * 
     * @param ristoranteService (not null)
     * @param jsonView (not null)
     */
    @Autowired
    public ActionsOnRistorantiController(RistoranteService ristoranteService,
            ActivityRistoranteService activityRistoranteService, MappingJacksonJsonView jsonView) {
        Assert.notNull(ristoranteService);
        Assert.notNull(jsonView);
        this.ristoranteService = ristoranteService;
        this.activityRistoranteService = activityRistoranteService;
        this.jsonView = jsonView;
    }

    /**
     * The Given user tried the the given restaurants
     * 
     * @param ristoId (not null)
     * @param model
     * @return the request risto
     */
    @Secured(EaterProfile.USER)
    @RequestMapping(value = "/security/iTriedRestaurants/{ristoId}")
    public ModelAndView addTriedRisto(@PathVariable String ristoId, Model model) {
        Assert.notNull(ristoId, "the ristoId cannot be null");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        Ristorante risto = ristoranteService.getByID(ristoId);
        activityRistoranteService.addTriedRisto(SecurityContextHelper.getAuthenticatedUser(), risto);
        modelAndView.addObject(new YouEatResponse("200"));
        return modelAndView;
    }

    /**
     * The Given user add or remove as favorite the given restaurants
     * 
     * @param ristoId (not null)
     * @param model
     * @return the request risto
     */
    @Secured(EaterProfile.USER)
    @RequestMapping(value = "/security/addOrRemoveRestaurantsAsFavorite/{ristoId}")
    public ModelAndView addOrRimoveRistoAsFavourite(@PathVariable String ristoId, Model model) {
        Assert.notNull(ristoId, "the ristoId cannot be null");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        Ristorante risto = ristoranteService.getByID(ristoId);
        activityRistoranteService.addOrRemoveRistoAsFavorite(SecurityContextHelper.getAuthenticatedUser(), risto);
        modelAndView.addObject(new YouEatResponse("200"));
        return modelAndView;
    }

    /**
     * Check if the given restaurant is a favorite for the logged user
     * 
     * @param model
     * @return a {@link YouEatBooleanResponse}
     */
    @RequestMapping(value = "/security/isFavoriteRestaurant/{ristoId}")
    @Secured(EaterProfile.USER)
    public ModelAndView isFavoriteRestaurant(@PathVariable String ristoId, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        Ristorante risto = ristoranteService.getByID(ristoId);
        boolean response = activityRistoranteService.isFavouriteRisto(SecurityContextHelper.getAuthenticatedUser(), risto);
        modelAndView.addObject(new YouEatBooleanResponse(response));
        return modelAndView;
    }

}