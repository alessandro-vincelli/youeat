package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.web.security.SecurityContextHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

/**
 * SignUp and logged user, works together with spring security filters
 * 
 * @author Alessandro Vincelli
 */
@Controller
public class SignInController {

    private MappingJacksonJsonView jsonView;

    /**
     * Constructor
     * 
     * @param jsonView (not null)
     */
    @Autowired
    public SignInController(MappingJacksonJsonView jsonView) {
        Assert.notNull(jsonView);
        this.jsonView = jsonView;
    }

    /**
     * Return the authenticated user
     * 
     * @param model
     * @return the authenticated user
     */
    @RequestMapping(value = {"/security/signUp"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Secured(EaterProfile.USER)
    public ModelAndView signUp(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(SecurityContextHelper.getAuthenticatedUser());
        return modelAndView;
    }
    
    @RequestMapping(value = {"/security/signUpFB"}, method = {RequestMethod.GET, RequestMethod.POST})
    @Secured(EaterProfile.USER)
    public ModelAndView signUpFB(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(SecurityContextHelper.getAuthenticatedUser());
        return modelAndView;
    }
}