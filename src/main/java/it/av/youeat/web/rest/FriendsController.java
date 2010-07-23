package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.util.PeriodUtil;
import it.av.youeat.web.security.SecurityContextHelper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * Provides some REST services to work with friends. Creates JSON objects.
 * 
 * @author Alessandro Vincelli
 */
@Controller
public class FriendsController {

    private ActivityRistoranteService activityRistoranteService;
    private EaterRelationService eaterRelationService;
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
    public FriendsController(ActivityRistoranteService activityRistoranteService, PeriodUtil periodUtil,
            MessageSource messageSource, MappingJacksonJsonView jsonView, EaterRelationService eaterRelationService) {
        Assert.notNull(activityRistoranteService);
        Assert.notNull(periodUtil);
        Assert.notNull(messageSource);
        Assert.notNull(jsonView);
        Assert.notNull(eaterRelationService);
        this.activityRistoranteService = activityRistoranteService;
        this.periodUtil = periodUtil;
        this.messageSource = messageSource;
        this.jsonView = jsonView;
        this.eaterRelationService = eaterRelationService;
    }

    /**
     * Returns the list of friends of the logged user
     * 
     * @param model
     * @return a list of Eater
     */
    @RequestMapping(value = "/security/getFriends")
    @Secured(EaterProfile.USER)
    public ModelAndView getFriendsr(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        List<Eater> friends = eaterRelationService.getFriendsAsEaters(SecurityContextHelper.getAuthenticatedUser());
        modelAndView.addObject(friends);
        return modelAndView;
    }

}