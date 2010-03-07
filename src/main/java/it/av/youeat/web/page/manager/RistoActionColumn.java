package it.av.youeat.web.page.manager;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.RistoranteService;

import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Provides the hide {@link org.apache.wicket.markup.html.link.Link} to open the Ristorante and shows the name of the
 * Ristprante
 * 
 * @author Alessandro Vincelli
 * 
 */
public class RistoActionColumn extends Panel {
    @SpringBean
    private RistoranteService ristoranteService;

    /**
     * @param id component id
     * @param model model for ristorante
     */
    public RistoActionColumn(String id, final IModel<Ristorante> model) {
        super(id, model);
        InjectorHolder.getInjector().inject(this);
        Link<Ristorante> link = new Link<Ristorante>("remove", model) {

            @Override
            public void onClick() {
                ristoranteService.remove(model.getObject());
                setResponsePage(getApplication().getHomePage());
                setRedirect(true);
            }
        };
        add(link);
    }

}