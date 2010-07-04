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
import it.av.youeat.web.data.UserSortableDataProvider;

import org.apache.wicket.IClusterable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.time.Duration;

/**
 * The panel provides the search form for ..
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class UserManagerUserSearchPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private SearchBean searchBean = new SearchBean();
    private Form<SearchBean> form;

    /**
     * Constructor
     * 
     * @param dataProvider
     * @param dataTable
     * @param id
     * @param feedbackPanel
     */
    public UserManagerUserSearchPanel(final UserSortableDataProvider dataProvider, final AjaxFallbackDefaultDataTable dataTable,
            String id, final FeedbackPanel feedbackPanel) {
        super(id);
        form = new Form<SearchBean>("searchForm", new CompoundPropertyModel<SearchBean>(searchBean));
        add(form);
        form.setOutputMarkupId(true);
        FormComponent<String> fc;
        fc = new TextField<String>("searchData");
        form.add(fc);
        // event and throttle it down to once per second
        AjaxFormValidatingBehavior.addToAllFormComponents(form, "onkeyup", Duration.ONE_SECOND);

        form.add(new AjaxButton("ajax-button", form) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                try {
                    dataProvider.setSearchData(((SearchBean) getForm().getModelObject()).getSearchData());
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

    public Form<SearchBean> getForm() {
        return form;
    }

}