package it.av.youeat.web.components;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.util.PeriodUtil;
import it.av.youeat.web.page.EaterViewPage;
import it.av.youeat.web.page.YoueatHttpParams;

import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class FriendEaterListView extends PropertyListView<ActivityRistorante> {
    @SpringBean
    private PeriodUtil periodUtil;

    /**
     * Generate a list of friend that eat in the given risto
     * 
     * @param id
     * @param list
     */
    public FriendEaterListView(String id, List<? extends ActivityRistorante> list) {
        super(id, list);
        Injector.get().inject(this);
    }

    @Override
    protected void populateItem(final ListItem<ActivityRistorante> item) {
        item.add(new Label("date.time", periodUtil.getPeriod(item.getModelObject().getDate().getTime(), getLocale())));
        BookmarkablePageLink eater = new BookmarkablePageLink("view-eater", EaterViewPage.class, new PageParameters(
                YoueatHttpParams.YOUEAT_ID + "=" + item.getModelObject().getEater().getId()));
        eater.add(new Label("eater.firstname"));
        eater.add(new Label("eater.lastname"));
        item.add(eater);
    }
}
