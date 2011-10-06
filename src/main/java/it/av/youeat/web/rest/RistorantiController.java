package it.av.youeat.web.rest;

import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.geo.Location;
import it.av.youeat.service.RistorantePositionService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.security.SecurityContextHelper;

import java.util.ArrayList;

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
 * Provides some REST services for restaurants. Creates JSON objects.
 * 
 * @author Alessandro Vincelli
 */
@Controller
public class RistorantiController {

    private RistoranteService ristoranteService;
    private RistorantePositionService positionService;
    private MappingJacksonJsonView jsonView;

    /**
     * Constructor
     * 
     * @param ristoranteService (not null)
     * @param positionService (not null)
     * @param jsonView (not null)
     */
    @Autowired
    public RistorantiController(RistoranteService ristoranteService, RistorantePositionService positionService,
            MappingJacksonJsonView jsonView) {
        Assert.notNull(ristoranteService);
        Assert.notNull(positionService);
        Assert.notNull(jsonView);
        this.ristoranteService = ristoranteService;
        this.jsonView = jsonView;
        this.positionService = positionService;
    }

    /**
     * Returns the last 10 restaurants as JSON objects
     * 
     * @param model
     * @return a list of ristoranti
     */
    @RequestMapping(value = "/ristoranti")
    public ModelAndView get(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(ristoranteService.getLastsAdded(10));
        return modelAndView;
    }

    /**
     * Returns a risto as a JSON object
     * 
     * @param ristoId (not null)
     * @param model
     * @return the request risto
     */
    @RequestMapping(value = "/getRistorante/{ristoId}")
    public ModelAndView getRistorante(@PathVariable String ristoId, Model model) {
        Assert.notNull(ristoId, "the ristoId cannot be null");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(ristoranteService.getByID(ristoId));
        return modelAndView;
    }

    /**
     * Searches restaurants using the given string
     * 
     * @param searchString min 3 characters (not null)
     * @param model
     * @return a list of found restaurants
     */
    @RequestMapping(value = "/findRistoranti/{searchString}")
    public ModelAndView findRistoranti(@PathVariable String searchString, Model model) {
        Assert.notNull(searchString, "the ristoId cannot be null");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        if (searchString.length() < 3) {
            modelAndView.addObject(new ArrayList<Ristorante>(0));
        } else {
            modelAndView.addObject(ristoranteService.freeTextSearch(searchString));
        }
        return modelAndView;
    }
    
    /**
     * Searches restaurants using the given string
     * 
     * @param searchString min 3 characters (not null)
     * @param firstResult the first result to retrieve, numbered from <tt>0</tt> 
     * @param maxResults
     * @param model
     * @return a list of found restaurants
     */
    @RequestMapping(value = "/findPaginatedRistoranti/{searchString}/{firstResult}/{maxResults}")
    public ModelAndView findPaginatedRistoranti(@PathVariable String searchString,  @PathVariable int firstResult, @PathVariable int maxResults, Model model) {
        Assert.notNull(searchString, "the search string cannot be null");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        if (searchString.length() < 3) {
            modelAndView.addObject(new ArrayList<Ristorante>(0));
        } else {
            modelAndView.addObject(ristoranteService.freeTextSearch(searchString, firstResult, maxResults));
        }
        return modelAndView;
    }

    /**
     * Returns restaurants as JSON objects sort by distance on the given position 
     * 
     * @param latitude
     * @param longitude
     * @param distanceInMeters 
     * @param maxResults
     * @param model
     * 
     * @return a list of ristoranti
     */
    @RequestMapping(value = "/findCloseRistoranti/{latitude}/{longitude}/{distanceInMeters}/{maxResults}")
    public ModelAndView getCloseRestaurants(@PathVariable Double latitude, @PathVariable Double longitude,
            @PathVariable Long distanceInMeters, @PathVariable int maxResults, Model model) {
        // http://localhost:8080/rest/findCloseRistoranti/42.5582722/12.6386542/900/10
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(positionService.around(new Location(latitude, longitude), distanceInMeters, 0, maxResults));
        return modelAndView;
    }
    
    /**
     * Returns restaurants as JSON objects sort by distance on the given position 
     * 
     * @param pattern
     * @param latitude
     * @param longitude
     * @param distanceInMeters 
     * @param maxResults
     * @param model
     * 
     * @return a list of ristoranti
     */
    @RequestMapping(value = "/findFreeTextSearchCloseRistoranti/{pattern}/{latitude}/{longitude}/{distanceInMeters}/{maxResults}")
    public ModelAndView findFreeTextSearch(@PathVariable String pattern, @PathVariable Double latitude, @PathVariable Double longitude,
            @PathVariable Long distanceInMeters, @PathVariable int maxResults, Model model) {
        // http://localhost:8080/rest/findFreeTextSearchCloseRistoranti/42.5582722/12.6386542/900/10
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(positionService.aroundFreeTextSearch(pattern, new Location(latitude, longitude), distanceInMeters, 0, maxResults));
        return modelAndView;
    }
    
    /**
     * Returns restaurants as JSON objects sort by distance on the given position
     * 
     * @param latitude
     * @param longitude
     * @param distanceInMeters
     * @param firstResult the first result to retrieve, numbered from <tt>0</tt> 
     * @param maxResults
     * @param model
     * 
     * @return a list of ristoranti
     */
    @RequestMapping(value = "/findPaginatedCloseRistoranti/{latitude}/{longitude}/{distanceInMeters}/{firstResult}/{maxResults}")
    public ModelAndView getCloseRestaurantsPaginated(@PathVariable Double latitude, @PathVariable Double longitude,
            @PathVariable Long distanceInMeters, @PathVariable int firstResult, @PathVariable int maxResults, Model model) {
        // http://localhost:8080/rest/findCloseRistoranti/42.5582722/12.6386542/900/10
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(positionService.around(new Location(latitude, longitude), distanceInMeters, firstResult, maxResults));
        return modelAndView;
    }

    /**
     * Returns user's favorite restaurants sorted by the distance between the risto and the given coordinates
     * 
     * @param model
     * @return a list of ristoranti
     */
    @RequestMapping(value = "/security/favorite/{latitude}/{longitude}/{user}")
    @Secured(EaterProfile.USER)
    public ModelAndView getFavorite(@PathVariable Double latitude, @PathVariable Double longitude, @PathVariable String user,
            Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(positionService.favourites(SecurityContextHelper.getAuthenticatedUser(), new Location(latitude,
                longitude), 0, 50));
        return modelAndView;
    }
}