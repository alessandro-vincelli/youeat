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

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.service.ActivityRistoranteService;
import it.av.youeat.web.components.ActivitiesListView;

import java.util.List;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Activities manager page
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "ADMIN" })
public class ActivitiesManagerPage extends BasePage {

    @SpringBean(name = "activityRistoranteService")
    private ActivityRistoranteService activityRistoranteService;

    private List<ActivityRistorante> activities;
    private WebMarkupContainer activitiesListContainer;
    private PropertyListView<ActivityRistorante> activitiesList;

    public ActivitiesManagerPage() {

        activities = activityRistoranteService.getLasts(50);
        activitiesListContainer = new WebMarkupContainer("activitiesListContainer");
        activitiesListContainer.setOutputMarkupId(true);
        add(activitiesListContainer);
        activitiesList = new ActivitiesListView("activitiesList", activities, true);
        activitiesList.setOutputMarkupId(true);
        activitiesListContainer.add(activitiesList);
        activitiesListContainer.setVisible(activities.size() > 0);
    }
}
