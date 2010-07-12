package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.EaterProfile;
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
 * SignUp and logged user, workd together with spring security filters  
 * 
 * @author Alessandro Vincelli
 */
@Controller
public class SignInController {

    private MappingJacksonJsonView jsonView;

    /**
     * Constructor
     * 
     * @param ristoranteService (not null)
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
    @RequestMapping(value = "/security/signUp")
    @Secured(EaterProfile.USER)
    public ModelAndView signUp(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(SecurityContextHelper.getAuthenticatedUser());
        return modelAndView;
    }
}