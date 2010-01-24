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
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.service.CityService;
import it.av.youeat.service.DataRistoranteService;
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.service.EaterService;
import it.av.youeat.web.data.UserSortableDataProvider;
import it.av.youeat.web.panel.SearchPanel;
import it.av.youeat.web.panel.UserTableActionPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Administration page for {@link Eater} bean.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( {"ADMIN"})
public class UserManagerPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean
    private EaterService userService;
    @SpringBean
    private EaterProfileService userProfileService;
    @SpringBean
    private DataRistoranteService dataRistoranteService;
    @SpringBean
    private CityService cityService;

    private AjaxFallbackDefaultDataTable<Eater> usersDataTable;
    private UserSortableDataProvider dataProvider;
    private Form<Eater> form;
    private SearchPanel searchPanel;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public UserManagerPage() throws YoueatException {

        form = new Form<Eater>("userForm", new CompoundPropertyModel<Eater>(new Eater()));
        form.setOutputMarkupId(true);
        form.add(new TextField<String>("password"));
        form.add(new TextField<String>("lastname"));
        form.add(new TextField<String>("firstname"));
        form.add(new TextField<String>("email"));
        form.add(new DropDownChoice<EaterProfile>("userProfile", new ArrayList<EaterProfile>(userProfileService
                .getAll()), new UserProfilesList()).setOutputMarkupId(true));

        form.add(new AjaxLink<Eater>("buttonClearForm", new Model<Eater>()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                form.setModelObject(new Eater());
                target.addComponent(form);
            }
        });
        form.add(new SubmitButton("ajax-button", form));
        add(form);

        List<IColumn<Eater>> columns = new ArrayList<IColumn<Eater>>();
        columns.add(new AbstractColumn<Eater>(new Model<String>(new StringResourceModel("datatableactionpanel.actions",
                this, null).getString())) {
            public void populateItem(Item<ICellPopulator<Eater>> cellItem, String componentId, IModel<Eater> model) {
                cellItem.add(new UserTableActionPanel(componentId, model));
            }
        });
        columns.add(new PropertyColumn<Eater>(new Model<String>(new StringResourceModel("firstname", this, null)
                .getString()), "firstname"));
        columns.add(new PropertyColumn<Eater>(new Model<String>(new StringResourceModel("lastname", this, null)
                .getString()), "lastname"));
        columns.add(new PropertyColumn<Eater>(new Model<String>(new StringResourceModel("email", this, null)
                .getString()), "email"));
        columns.add(new PropertyColumn<Eater>(new Model<String>(new StringResourceModel("userProfile", this, null)
                .getString()), "userProfile.name"));
        dataProvider = new UserSortableDataProvider();
        usersDataTable = new AjaxFallbackDefaultDataTable<Eater>("usersDataTable", columns, dataProvider, 10);
        add(usersDataTable);
        searchPanel = new SearchPanel(dataProvider, usersDataTable, "searchPanel", getFeedbackPanel());
        add(searchPanel);
        
        add(new AjaxLink<Eater>("indexDataRistorante", new Model<Eater>()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dataRistoranteService.indexData();
            }
        });
        add(new AjaxLink<Eater>("indexCity", new Model<Eater>()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                cityService.getAll();
            }
        });
    }

    private class SubmitButton extends AjaxButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<Eater> form) {
            super(id, form);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (StringUtils.isBlank(form.getModelObject().getId())) {
                tag.getAttributes().put("value", new StringResourceModel("button.create", this, null).getString());
            } else {
                tag.getAttributes().put("value", new StringResourceModel("button.update", this, null).getString());
            }
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                Eater userToSave = (Eater) form.getModelObject();
                if (StringUtils.isNotBlank(userToSave.getId())) {
                    userToSave = userService.update(userToSave);
                    getFeedbackPanel().info(new StringResourceModel("info.userupdated", this, null).getString());
                } else {
                    userToSave = userService.add(userToSave);
                    getFeedbackPanel().info(new StringResourceModel("info.useradded", this, null).getString());
                }
                form.setModelObject(userToSave);
                refreshDataTable();
                target.addComponent(usersDataTable);
                target.addComponent(form);
            } catch (YoueatException e) {
                getFeedbackPanel().error(getString("genericErrorMessage"));
            }
            target.addComponent(getFeedbackPanel());
        }

        @Override
        protected void onError(AjaxRequestTarget target, Form form) {
            getFeedbackPanel().anyErrorMessage();
            target.addComponent(getFeedbackPanel());
        }
    }

    /**
     * Fill with fresh data the repetear
     * 
     * @throws YoueatException
     */
    public final void refreshDataTable() throws YoueatException {
        dataProvider.fetchResults(searchPanel.getForm().getModelObject().getSearchData());
    }

    public final Form<Eater> getForm() {
        return form;
    }

    public final EaterService getUsersServices() {
        return userService;
    }

    public final AjaxFallbackDefaultDataTable<Eater> getUsersDataTable() {
        return usersDataTable;
    }

    private class UserProfilesList implements IChoiceRenderer<EaterProfile> {
        private static final long serialVersionUID = 1L;

        @Override
        public Object getDisplayValue(EaterProfile object) {
            return object.getName();
        }

        @Override
        public String getIdValue(EaterProfile object, int index) {
            return object.getName();
        }

    }
}