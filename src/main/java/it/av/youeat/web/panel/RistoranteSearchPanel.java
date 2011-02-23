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
package it.av.youeat.web.panel;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.components.RistoranteDataTable;
import it.av.youeat.web.data.RistoranteSortableDataProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

/**
 * The panel provides the search form for ..
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class RistoranteSearchPanel extends Panel {
    private SearchBean searchBean = new SearchBean();

    /**
     * Constructor
     * 
     * @param id
     * @param dataProvider
     * @param dataTable
     * @param feedbackPanel
     */
    public RistoranteSearchPanel(String id, final RistoranteSortableDataProvider dataProvider,
            final RistoranteDataTable<Ristorante> dataTable, final FeedbackPanel feedbackPanel) {
        super(id);
        Form<String> form = new Form<String>("searchForm", new CompoundPropertyModel(searchBean));
        add(form);
        form.setOutputMarkupId(true);
        FormComponent<String> fc;
        // fc = new TextField<String>("searchData");
        AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
        autoCompleteSettings.setCssClassName("autocomplete-risto");
        autoCompleteSettings.setAdjustInputWidth(false);
        fc = new SearchBox("searchData", autoCompleteSettings);
        form.add(fc);
        // event and throttle it down to once per second
        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.ONE_SECOND);

        form.add(new AjaxButton("ajax-button", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                try {
                    String pattern = getRequest().getParameter("searchData");
                    dataProvider.fetchResults(pattern, dataTable.getRowsPerPage());
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
     * 
     * Simple Bean to store the Form data
     * 
     */
    public static class SearchBean implements IClusterable {
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

    private static class SearchBox extends AutoCompleteTextField<String> {

        @SpringBean
        private RistoranteService ristoranteService;

        public SearchBox(String id, AutoCompleteSettings autoCompleteSettings) {
            super(id, autoCompleteSettings);
            InjectorHolder.getInjector().inject(this);
        }

        @Override
        protected Iterator<String> getChoices(String input) {
            Collection<String> choises = new ArrayList<String>();
            try {
                if (!input.isEmpty() && input.length() > 2){
                    for (Ristorante risto : ristoranteService.freeTextSearch(input, -1, 25)) {
                        choises.add(risto.getName() + " <i>(" + risto.getCity() + ")</i>" );
                    }
                }
            } catch (YoueatException e) {
            }
            return choises.iterator();
        }
    }

}
