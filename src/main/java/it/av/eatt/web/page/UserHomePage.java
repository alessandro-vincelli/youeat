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
package it.av.eatt.web.page;

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.ActivityRistorante;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.service.ActivityRistoranteService;
import it.av.eatt.web.commons.ActivityPaging;
import it.av.eatt.web.components.ActivitiesListView;
import it.av.eatt.web.components.RistoNameColumn;
import it.av.eatt.web.components.RistoranteDataTable;
import it.av.eatt.web.data.RistoranteSortableDataProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Personal user home page.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "USER" })
public class UserHomePage extends BasePage {
    private static final long serialVersionUID = 1L;
    @SpringBean(name = "activityRistoranteService")
    private ActivityRistoranteService activityRistoranteService;

    private ActivityPaging activityPagingUser = new ActivityPaging(0, 15);
    private ActivityPaging activityPagingFriends = new ActivityPaging(0, 15);
    private List<ActivityRistorante> activities;
    private WebMarkupContainer activitiesListContainer;
    private PropertyListView<ActivityRistorante> activitiesList;
    private List<ActivityRistorante> friendsActivities;
    private WebMarkupContainer friendsActivitiesListContainer;

    public UserHomePage() {
        RistoranteSortableDataProvider ristoranteSortableDataProvider = new RistoranteSortableDataProvider();
        List<IColumn<Ristorante>> columns = new ArrayList<IColumn<Ristorante>>();
        columns.add(new AbstractColumn<Ristorante>(new Model<String>(new StringResourceModel(
                "datatableactionpanel.actions", this, null).getString())) {
            public void populateItem(Item<ICellPopulator<Ristorante>> cellItem, String componentId,
                    IModel<Ristorante> model) {
                cellItem.add(new RistoNameColumn(componentId, model));
            }

            @Override
            public String getCssClass() {
                return "ristoName";
            }

            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });

        columns.add(new PropertyColumn<Ristorante>(new Model<String>(new StringResourceModel("city", this, null)
                .getString()), "city") {
            @Override
            public Component getHeader(String componentId) {
                return super.getHeader(componentId).setVisible(false);
            }
        });

        RistoranteDataTable<Ristorante> ristoranteDataTable = new RistoranteDataTable<Ristorante>(
                "ristoranteDataTable", columns, ristoranteSortableDataProvider, 10);
        add(ristoranteDataTable);

        RistoranteSearchPanel ristoranteSearchPanel = new RistoranteSearchPanel("ristoranteSearchPanel",
                ristoranteSortableDataProvider, ristoranteDataTable, getFeedbackPanel());
        add(ristoranteSearchPanel);

        // User activities
        try {
            activities = activityRistoranteService.findByUser(getLoggedInUser(), activityPagingUser.getFirstResult(),
                    activityPagingUser.getMaxResults());
        } catch (JackWicketException e) {
            activities = new ArrayList<ActivityRistorante>();
            error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
        }

        activitiesListContainer = new WebMarkupContainer("activitiesListContainer");
        activitiesListContainer.setOutputMarkupId(true);
        add(activitiesListContainer);
        activitiesList = new ActivitiesListView("activitiesList", activities, false);
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
                } catch (JackWicketException e) {
                    error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
                }
            }
        };
        activitiesListContainer.add(moreActivitiesLink);
        // Friends activities
        try {
            friendsActivities = activityRistoranteService.findByUserFriend(getLoggedInUser(), 0, 3);
        } catch (JackWicketException e) {
            friendsActivities = new ArrayList<ActivityRistorante>();
            error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
        }

        friendsActivitiesListContainer = new WebMarkupContainer("friendsActivitiesListContainer");
        friendsActivitiesListContainer.setOutputMarkupId(true);
        add(friendsActivitiesListContainer);
        PropertyListView<ActivityRistorante> friendsActivitiesList = new ActivitiesListView(
                "friendsActivitiesList", new ArrayList<ActivityRistorante>(friendsActivities), true);
        friendsActivitiesListContainer.add(friendsActivitiesList);
        AjaxFallbackLink<String> moreFriendsActivitiesLink = new AjaxFallbackLink<String>("moreFriendsActivitiesLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                activityPagingFriends.addNewPage();
                try {
                    friendsActivities.addAll(activityRistoranteService.findByUserFriend(getLoggedInUser(),
                            activityPagingFriends.getFirstResult(), activityPagingFriends.getMaxResults()));
                    if (target != null) {
                        target.addComponent(friendsActivitiesListContainer);
                    }
                } catch (JackWicketException e) {
                    error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
                }
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                if (friendsActivities.size() == 0) {
                    setVisible(false);
                }
            }

        };
        friendsActivitiesListContainer.add(moreFriendsActivitiesLink);
    }

}
