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

import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.ActivityRistorante;
import it.av.eatt.service.ActivityRistoranteService;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Show informations and activities of one Eater.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER" })
public class UserExplorerPage extends BasePage {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private ActivityRistoranteService activityRistoranteService;

    public UserExplorerPage() {

        Collection<ActivityRistorante> activities;
        try {
            // FIXME pass the correct user
            activities = activityRistoranteService.findByUser(null);
        } catch (YoueatException e) {
            activities = new ArrayList<ActivityRistorante>();
            error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
        }

        PropertyListView<ActivityRistorante> activitiesList = new PropertyListView<ActivityRistorante>(
                "activitiesList", new ArrayList<ActivityRistorante>(activities)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<ActivityRistorante> item) {
                item.add(new Label("user.lastname"));
                item.add(new Label("date.time"));
                item.add(new Label("ristorantePath"));
            }
        };
        add(activitiesList);
    }

}
