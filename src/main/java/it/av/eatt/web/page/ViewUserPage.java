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
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.util.DateUtil;
import it.av.eatt.service.ActivityRistoranteService;
import it.av.eatt.service.EaterService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
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
public class ViewUserPage extends BasePage {
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


    public ViewUserPage(PageParameters pageParameters) throws JackWicketException {
        if (!pageParameters.containsKey(YoueatHttpParams.PARAM_YOUEAT_ID)) {
            throw new JackWicketException("Missing user id");
        }

        String eaterId = pageParameters.getString(YoueatHttpParams.PARAM_YOUEAT_ID, "");        
        Eater eater = eaterService.getByID(eaterId);

        add(new Label("eater", eater.getFirstname() + " "+ eater.getLastname()));
        
        // User activities
        try {
            activities = activityRistoranteService.findByUser(eater, activityPagingUser.getFirstResult(),
                    activityPagingUser.getMaxResults());
        } catch (JackWicketException e) {
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
                item.add(createActivityIcon(item));
                item.add(new Label("date.time", DateUtil.getPeriod(item.getModelObject().getDate().getTime())));
                BookmarkablePageLink<String> ristoLink = new BookmarkablePageLink<String>("ristorante.link",
                        RistoranteViewPage.class, new PageParameters("ristoranteId="
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
                } catch (JackWicketException e) {
                    error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
                }
            }
        };
        activitiesListContainer.add(moreActivitiesLink);
    }

    private Image createActivityIcon(final ListItem<ActivityRistorante> item) {
        // default activity icon is plus
        ResourceReference img = new ResourceReference(this.getClass(), "resources/images/plus_64.png");
        if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_MODIFICATION)) {
            img = new ResourceReference(this.getClass(), "resources/images/pencil_64.png");
        } else if (item.getModelObject().getType().equals(ActivityRistorante.TYPE_VOTED)) {
            img = new ResourceReference(this.getClass(), "resources/images/tick_64.png");
        }
        return new Image("type", img) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.getAttributes().put("alt", item.getModelObject().getType());
                tag.getAttributes().put("title", item.getModelObject().getType());
            }
        };
    }

    private class ActivityPaging implements Serializable {
        private int firstResult;
        private int maxResults;
        private int page;

        /**
         * @param firstResult
         * @param maxResults
         */
        public ActivityPaging(int firstResult, int maxResults) {
            super();
            this.firstResult = firstResult;
            this.maxResults = maxResults;
            this.page = 0;
        }

        public ActivityPaging addNewPage() {
            this.setPage(this.getPage() + 1);
            int offset = this.getMaxResults() * this.getPage();
            this.setFirstResult(this.getFirstResult() + offset);
            return this;
        }

        /**
         * @return the firstResult
         */
        public int getFirstResult() {
            return firstResult;
        }

        /**
         * @param firstResult the firstResult to set
         */
        public void setFirstResult(int firstResult) {
            this.firstResult = firstResult;
        }

        /**
         * @return the maxResults
         */
        public int getMaxResults() {
            return maxResults;
        }

        /**
         * @param maxResults the maxResults to set
         */
        public void setMaxResults(int maxResults) {
            this.maxResults = maxResults;
        }

        /**
         * @return the page
         */
        public int getPage() {
            return page;
        }

        /**
         * @param page the page to set
         */
        public void setPage(int page) {
            this.page = page;
        }

    }
}
