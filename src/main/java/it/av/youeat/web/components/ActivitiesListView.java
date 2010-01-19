package it.av.youeat.web.components;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.web.commons.ActivityCommons;
import it.av.youeat.web.commons.YoueatHttpParams;
import it.av.youeat.web.page.EaterViewPage;
import it.av.youeat.web.page.RistoranteViewPage;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

public class ActivitiesListView extends PropertyListView<ActivityRistorante> {

    private static final long serialVersionUID = 1L;
    private boolean showEater;

    /**
     * Generate a list of activities
     * 
     * @param id
     * @param list
     * @showEater true to show the actor of the activity
     */
    public ActivitiesListView(String id, List<? extends ActivityRistorante> list, boolean showEater) {
        super(id, list);
        this.showEater = showEater;
    }

    @Override
    protected void populateItem(final ListItem<ActivityRistorante> item) {
        item.add(ActivityCommons.createActivityIcon(getPage().getClass(), item));
        item.add(new Label("date.time", DateUtil.getPeriod(item.getModelObject().getDate().getTime())));
        BookmarkablePageLink<String> ristoLink = new BookmarkablePageLink<String>("ristorante.link",
                RistoranteViewPage.class, new PageParameters(YoueatHttpParams.RISTORANTE_ID + "="
                        + item.getModelObject().getRistorante().getId()));
        ristoLink.add(new Label("ristorante.name"));
        item.add(ristoLink);
        item.add(new Label("activityDesc", getString(item.getModelObject().getType())));
        AjaxFallbackLink<String> eater = new AjaxFallbackLink<String>("view-eater") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PageParameters pp = new PageParameters(YoueatHttpParams.USER_ID + "="
                        + item.getModelObject().getEater().getId());
                setResponsePage(EaterViewPage.class, pp);
            }
        };
        eater.add(new Label("eater.lastname"));
        if(showEater){
            item.add(eater);
        }
    }

    public boolean isShowEater() {
        return showEater;
    }

    public void setShowEater(boolean showEater) {
        this.showEater = showEater;
    }
}