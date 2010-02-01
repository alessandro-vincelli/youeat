package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.util.RistoranteUtil;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Provides the hide {@link Link} to open the Ristorante and shows the name of the Ristprante
 * 
 * @author Alessandro Vincelli
 * 
 */
public class RistoNameColumn extends Panel {

    /**
     * @param id component id
     * @param model model for ristorante
     */
    public RistoNameColumn(String id, final IModel<Ristorante> model) {
        super(id, model);
        Link<Ristorante> link = new Link<Ristorante>("ristoLink", model) {

            @Override
            public void onClick() {
                setResponsePage(RistoranteViewPage.class, RistoranteUtil.createParamsForRisto(getModelObject()));
            }
        };
        link.add(new Label("name", model.getObject().getName()));
        add(link);
    }

}