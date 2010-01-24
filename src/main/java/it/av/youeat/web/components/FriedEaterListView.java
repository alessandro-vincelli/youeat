package it.av.youeat.web.components;

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.util.PeriodUtil;
import it.av.youeat.web.page.EaterViewPage;
import it.av.youeat.web.page.YoueatHttpParams;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class FriedEaterListView extends PropertyListView<ActivityRistorante> {
    @SpringBean
    private PeriodUtil periodUtil;
    
    /**
     * Generate a list of friend that eat in the given risto
     * 
     * @param id
     * @param list
     */
    public FriedEaterListView(String id, List<? extends ActivityRistorante> list) {
        super(id, list);
        InjectorHolder.getInjector().inject(this);
    }

    @Override
    protected void populateItem(final ListItem<ActivityRistorante> item) {
        item.add(new Label("date.time", periodUtil.getPeriod(item.getModelObject().getDate().getTime(), getLocale())));
        AjaxFallbackLink<String> eater = new AjaxFallbackLink<String>("view-eater") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                PageParameters pp = new PageParameters(YoueatHttpParams.YOUEAT_ID + "="
                        + item.getModelObject().getEater().getId());
                setResponsePage(EaterViewPage.class, pp);
            }
        };
        eater.add(new Label("eater.firstname"));
        eater.add(new Label("eater.lastname"));
        item.add(eater);
    }
}
