package it.av.eatt.web.components;

import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.web.commons.YoueatHttpParams;
import it.av.eatt.web.page.RistoranteViewPage;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

public class RistosListView extends PropertyListView<Ristorante> {

    private static final long serialVersionUID = 1L;

    /**
     * Generate a list of ristos
     * 
     * @param id
     * @param list
     * @showEater true to show the actor of the activity
     */
    public RistosListView(String id, List<? extends Ristorante> list) {
        super(id, list);
    }

    @Override
    protected void populateItem(final ListItem<Ristorante> item) {
        BookmarkablePageLink<String> ristoLink = new BookmarkablePageLink<String>("ristorante.link",
                RistoranteViewPage.class, new PageParameters(YoueatHttpParams.RISTORANTE_ID + "="
                        + item.getModelObject().getId()));
        StringBuffer risto = new StringBuffer();
        risto.append(item.getModelObject().getName());
        risto.append(" <i>(");
        risto.append(item.getModelObject().getCity());
        risto.append("</i>)");
        ristoLink.add(new Label("name", risto.toString()).setEscapeModelStrings(false));
        item.add(ristoLink);

    }
}