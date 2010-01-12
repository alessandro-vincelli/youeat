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
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.service.EaterService;
import it.av.eatt.web.data.SearchUserFriendSortableDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.springframework.util.Assert;

/**
 * The panel provides the search form for ..
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SearchFriendPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private SearchBean searchBean = new SearchBean();

    /**
     * Constructor
     * 
     * @param dataProvider
     * @param dataTable
     * @param id
     * @param feedbackPanel
     * @param loggedUser
     */
    public SearchFriendPanel(final SearchUserFriendSortableDataProvider dataProvider,
            final AjaxFallbackDefaultDataTable dataTable, String id, final FeedbackPanel feedbackPanel, Eater loggedUser) {
        super(id);
        Form<String> form = new Form<String>("searchForm", new CompoundPropertyModel(searchBean));
        add(form);
        form.setOutputMarkupId(true);
        FormComponent<String> fc;
        AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
        autoCompleteSettings.setCssClassName("autocomplete-risto");
        autoCompleteSettings.setAdjustInputWidth(false);
        fc = new SearchBox("searchData", loggedUser, autoCompleteSettings);
        //fc = new TextField<String>();
        form.add(fc);
        // event and throttle it down to once per second
        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.ONE_SECOND);

        form.add(new AjaxButton("ajax-button", form) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                String pattern = getRequest().getParameter("searchData");
                try {
                    dataProvider.fetchResults(pattern);
                } catch (YoueatException e) {
                    feedbackPanel.error(e.getMessage());
                }
                target.addComponent(dataTable);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                target.addComponent(dataTable);
            }
        });
    }

    /**
     * Simple Bean to store the Form data
     * 
     * @author Alessandro Vincelli
     */
    public static class SearchBean implements IClusterable {
        private static final long serialVersionUID = 1L;
        private String searchData;

        /**
         * @return the searchData
         */
        public final String getSearchData() {
            return searchData;
        }

        /**
         * @param searchData the searchData to set
         */
        public final void setSearchData(String searchData) {
            this.searchData = searchData;
        }
    }

    public SearchBean getSearchBean() {
        return searchBean;
    }

    private static class SearchBox extends AutoCompleteTextField<String> {
        private static final long serialVersionUID = 1L;
        @SpringBean
        private EaterService eaterService;
        private Eater loggedUser;

        /**
         * @param id
         * @param loggedUser  actual logged user (not null)
         * @param autoCompleteSettings
         */
        public SearchBox(String id, Eater loggedUser, AutoCompleteSettings autoCompleteSettings) {
            super(id, autoCompleteSettings);
            this.loggedUser = loggedUser;
            InjectorHolder.getInjector().inject(this);
            Assert.notNull(loggedUser);
        }

        @Override
        protected Iterator<String> getChoices(String input) {
            Collection<String> choises = new ArrayList<String>();
            try {
                if (!input.isEmpty() && input.length() > 2)
                    for (Eater eater : eaterService.findUserWithoutRelation(loggedUser, input + "~")) {
                        choises.add(eater.getFirstname() + " " + eater.getLastname() );
                    }
            } catch (YoueatException e) {
            }
            return choises.iterator();
        }
    }
}
