package it.av.youeat.web.page.manager;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.RistoranteService;

import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Provides some actions to manage the current restaurant
 * 
 * @author Alessandro Vincelli
 * 
 */
public class RistoActionColumn extends Panel {
    @SpringBean
    private RistoranteService ristoranteService;
    @SpringBean
    private EaterService eaterService;

    /**
     * @param id component id
     * @param model model for ristorante
     */
    public RistoActionColumn(String id, final IModel<Ristorante> model) {
        super(id, model);
        Injector.get().inject(this);
        Link<Ristorante> link = new Link<Ristorante>("remove", model) {

            @Override
            public void onClick() {
                ristoranteService.remove(model.getObject());
                setResponsePage(getApplication().getHomePage());
                //TODO 1.5
                //setRedirect(true);
            }
        };
        add(link);

        Form<String> form = new Form<String>("setRestaurateurForm", new CompoundPropertyModel(model));
        form.setOutputMarkupId(true);
        add(form);
        ArrayList<Eater> eaters = new ArrayList<Eater>(eaterService.getAll());
        eaters.add(null);
        DropDownChoice<Eater> restaurateur = new DropDownChoice<Eater>("restaurateur", eaters);
        restaurateur.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                ristoranteService.updateNoRevision(model.getObject());
            }
        });
        form.add(restaurateur);

    }

}