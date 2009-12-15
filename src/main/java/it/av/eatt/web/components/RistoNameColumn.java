package it.av.eatt.web.components;

import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.web.commons.YoueatHttpParams;
import it.av.eatt.web.page.RistoranteViewPage;

import java.util.HashMap;

import org.apache.wicket.PageParameters;
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
                HashMap<String, String> parameterMap = new HashMap<String, String>();
                parameterMap.put(YoueatHttpParams.RISTORANTE_ID, getModelObject().getId());
                setResponsePage(RistoranteViewPage.class, new PageParameters(parameterMap));
            }
        };
        link.add(new Label("name", model.getObject().getName()));
        add(link);
    }

}