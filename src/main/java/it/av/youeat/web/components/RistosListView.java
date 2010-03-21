package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.page.RistoranteViewPage;
import it.av.youeat.web.util.RistoranteUtil;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

public class RistosListView extends PropertyListView<Ristorante> {


    /**
     * Generate a list of ristos
     * 
     * @param id
     * @param list
     * @showEater true to show the actor of the activity
     */
    public RistosListView(String id, List<? extends Ristorante> list) {
        super(id, list);
        if(list.isEmpty()){
            setVisible(false);
        }
    }

    @Override
    protected void populateItem(final ListItem<Ristorante> item) {
        BookmarkablePageLink<String> ristoLink = new BookmarkablePageLink<String>("ristorante.link",
                RistoranteViewPage.class, RistoranteUtil.createParamsForRisto(item.getModelObject()));
        StringBuffer risto = new StringBuffer();
        risto.append(item.getModelObject().getName());
        risto.append(" <i>(");
        risto.append(item.getModelObject().getCity());
        risto.append("</i>)");
        ristoLink.add(new Label("name", risto.toString()).setEscapeModelStrings(false));
        item.add(ristoLink);

    }
}
