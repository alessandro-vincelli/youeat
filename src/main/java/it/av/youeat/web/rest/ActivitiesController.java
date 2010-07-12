package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.web.security.SecurityContextHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
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
    private MappingJacksonJsonView jsonView;

    /**
     * Constructor
     * 
     * @param activityRistoranteService (not null)
     * @param jsonView (not null)
     */
    @Autowired
    public ActivitiesController(ActivityRistoranteService activityRistoranteService, MappingJacksonJsonView jsonView) {
        Assert.notNull(activityRistoranteService);
        Assert.notNull(jsonView);
        this.activityRistoranteService = activityRistoranteService;
        this.jsonView = jsonView;
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
        modelAndView.addObject(activityRistoranteService.findByUserFriendAndUser(SecurityContextHelper.getAuthenticatedUser(), 0, 20));
        return modelAndView;
    }

}