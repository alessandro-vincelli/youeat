package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.util.RistoranteUtil;

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
        setResponsePage(RistoranteViewPage.class, RistoranteUtil.createParamsForRisto(getModelObject()));
    }
}
