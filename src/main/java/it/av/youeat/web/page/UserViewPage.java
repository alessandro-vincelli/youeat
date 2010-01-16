/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.web.page;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.service.EaterService;
import it.av.youeat.web.commons.ActivityCommons;
import it.av.youeat.web.commons.ActivityPaging;
import it.av.youeat.web.commons.YoueatHttpParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Personal user home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER" })
public class UserViewPage extends BasePage {
    private static final long serialVersionUID = 1L;
    @SpringBean(name = "activityRistoranteService")
    private ActivityRistoranteService activityRistoranteService;
    @SpringBean
    private EaterService eaterService;

    private ActivityPaging activityPagingUser = new ActivityPaging(0, 20);
    private List<ActivityRistorante> activities;
    private WebMarkupContainer activitiesListContainer;
    private PropertyListView<ActivityRistorante> activitiesList;
    Collection<ActivityRistorante> friendsActivities;


    public UserViewPage(PageParameters pageParameters) throws YoueatException {
        if (!pageParameters.containsKey(YoueatHttpParams.USER_ID)) {
            throw new YoueatException("Missing user id");
        }

        String eaterId = pageParameters.getString(YoueatHttpParams.USER_ID, "");        
        Eater eater = eaterService.getByID(eaterId);

        add(new Label("eater", eater.getFirstname() + " "+ eater.getLastname()));
        
        // User activities
        try {
            activities = activityRistoranteService.findByUser(eater, activityPagingUser.getFirstResult(),
                    activityPagingUser.getMaxResults());
        } catch (YoueatException e) {
            activities = new ArrayList<ActivityRistorante>();
            error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
        }

        activitiesListContainer = new WebMarkupContainer("activitiesListContainer");
        activitiesListContainer.setOutputMarkupId(true);
        add(activitiesListContainer);
        activitiesList = new PropertyListView<ActivityRistorante>("activitiesList", activities) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<ActivityRistorante> item) {
                item.add(ActivityCommons.createActivityIcon(getPage().getClass(), item));
                item.add(new Label("date.time", DateUtil.getPeriod(item.getModelObject().getDate().getTime())));
                BookmarkablePageLink<String> ristoLink = new BookmarkablePageLink<String>("ristorante.link",
                        RistoranteViewPage.class, new PageParameters(YoueatHttpParams.RISTORANTE_ID + "="
                                + item.getModelObject().getRistorante().getId()));
                ristoLink.add(new Label("ristorante.name"));
                item.add(ristoLink);
            }
        };
        activitiesList.setOutputMarkupId(true);
        activitiesListContainer.add(activitiesList);
        AjaxFallbackLink<String> moreActivitiesLink = new AjaxFallbackLink<String>("moreActivities") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                activityPagingUser.addNewPage();
                try {
                    activities.addAll(activityRistoranteService.findByUser(getLoggedInUser(), activityPagingUser
                            .getFirstResult(), activityPagingUser.getMaxResults()));
                    if (target != null) {
                        target.addComponent(activitiesListContainer);
                    }
                } catch (YoueatException e) {
                    error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
                }
            }
        };
        activitiesListContainer.add(moreActivitiesLink);
    }
}
