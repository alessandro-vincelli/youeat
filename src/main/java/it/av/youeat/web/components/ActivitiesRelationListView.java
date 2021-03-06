package it.av.youeat.web.components;

import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;
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

public class ActivitiesRelationListView extends PropertyListView<ActivityEaterRelation> {

    @SpringBean
    private PeriodUtil periodUtil;

    /**
     * Generate a list of activities
     * 
     * @param id
     * @param list
     */
    public ActivitiesRelationListView(String id, List<? extends ActivityEaterRelation> list) {
        super(id, list);
        Injector.get().inject(this);
    }

    @Override
    protected void populateItem(final ListItem<ActivityEaterRelation> item) {
        // item.add(ActivityCommons.createActivityIcon(getPage().getClass(), item));
        item.add(new Label("date.time", periodUtil.getPeriod(item.getModelObject().getDate().getTime(), getLocale())));
        Eater eater1 = item.getModelObject().getEater();
        Eater eater2 = item.getModelObject().getWithUser();
        BookmarkablePageLink<String> eater1Link = new BookmarkablePageLink<String>("eater1.link", EaterViewPage.class,
                new PageParameters(YoueatHttpParams.YOUEAT_ID + "=" + eater1.getId()));
        eater1Link.add(new Label("eater.name", eater1.toString()));
        item.add(eater1Link);
        BookmarkablePageLink<String> eater2Link = new BookmarkablePageLink<String>("eater2.link", EaterViewPage.class,
                new PageParameters(YoueatHttpParams.YOUEAT_ID + "=" + eater2.getId()));
        eater2Link.add(new Label("eater.name", eater2.toString()));
        item.add(eater2Link);
        //dirty solution
        if(item.getModelObject().getEaterActivityType().equals(ActivityEaterRelation.TYPE_ARE_FRIENDS)){
            item.add(new Label("activityDesc", getString(item.getModelObject().getEaterActivityType())));
            item.add(new Label("and", getString("and")));    
        }
        else{
            item.add(new Label("activityDesc", "").setVisible(false));
            item.add(new Label("and", getString(item.getModelObject().getEaterActivityType())));
        }
        
    }

}
