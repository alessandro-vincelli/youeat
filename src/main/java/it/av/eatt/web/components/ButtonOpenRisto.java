package it.av.eatt.web.components;

import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.web.commons.YoueatHttpParams;
import it.av.eatt.web.page.RistoranteViewPage;

import java.util.HashMap;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.model.IModel;

/**
 * Create a button to open the given risto
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ButtonOpenRisto extends AjaxFallbackLink<Ristorante> {
    public ButtonOpenRisto(String id, IModel<Ristorante> risto, boolean visibleByDefault) {
        super(id, risto);
        setOutputMarkupId(true);
        if (!visibleByDefault) {
            setVisible(false);
            setOutputMarkupPlaceholderTag(true);
        }
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(YoueatHttpParams.RISTORANTE_ID, getModelObject().getId());
        setResponsePage(RistoranteViewPage.class, new PageParameters(parameterMap));
    }
}
