package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.util.RistoranteUtil;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;

/**
 * Create a links to open the given risto
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ButtonOpenRisto extends BookmarkablePageLink<Ristorante> {
    public ButtonOpenRisto(String id, IModel<Ristorante> risto, boolean visibleByDefault) {
        super(id, RistoranteViewPage.class);
        setModel(risto);
        setOutputMarkupId(true);
        if (!visibleByDefault) {
            setVisible(false);
            setOutputMarkupPlaceholderTag(true);
        }
    }

    @Override
    public PageParameters getPageParameters() {
        // necessary because some time the button is created without a risto, see newRistoPage
        return RistoranteUtil.createParamsForRisto(getModelObject());
    }

}
