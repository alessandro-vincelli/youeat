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
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.service.EaterProfileService;
import it.av.youeat.web.data.UserProfileSortableDataProvider;

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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * The page provides some basic operation on the {@link EaterProfile} bean.
 * 
 * <ul>
 * <li>{@link Form} to edit and add {@link EaterProfile} with ajax buttons</li>
 * <li>{@link AjaxFallbackDefaultDataTable} to show and enable operations on the {@link EaterProfile}</li>
 * </ul>
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@AuthorizeInstantiation( { "ADMIN" })
public class UserProfilePage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "userProfileService")
    private EaterProfileService userProfileService;

    private EaterProfile userProfile;
    private AjaxFallbackDefaultDataTable<EaterProfile> usersProfileDataTable;
    private UserProfileSortableDataProvider dataProvider;
    private Form<EaterProfile> form;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public UserProfilePage() throws YoueatException {
        userProfile = new EaterProfile();

        form = new Form<EaterProfile>("userProfileForm", new CompoundPropertyModel<EaterProfile>(userProfile));
        form.setOutputMarkupId(true);
        form.add(new RequiredTextField<String>("name"));
        form.add(new TextField<String>("description"));

        form.add(new AjaxLink<EaterProfile>("buttonClearForm", new Model<EaterProfile>(userProfile)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                form.setModelObject(new EaterProfile());
                target.addComponent(form);
            }
        });
        form.add(new SubmitButton("ajax-button", form));
        add(form);

        List<IColumn<EaterProfile>> columns = new ArrayList<IColumn<EaterProfile>>();
        columns.add(new AbstractColumn<EaterProfile>(new Model<String>(new StringResourceModel(
                "datatableactionpanel.actions", this, null).getString())) {
            public void populateItem(Item<ICellPopulator<EaterProfile>> cellItem, String componentId,
                    IModel<EaterProfile> model) {
                cellItem.add(new UserProfileTableActionPanel(componentId, model));
            }
        });
        columns.add(new PropertyColumn<EaterProfile>(new Model<String>(new StringResourceModel("name", this, null)
                .getString()), "name"));
        columns.add(new PropertyColumn<EaterProfile>(new Model<String>(new StringResourceModel("description", this,
                null).getString()), "description"));
        dataProvider = new UserProfileSortableDataProvider();
        refreshDataTable();
        usersProfileDataTable = new AjaxFallbackDefaultDataTable<EaterProfile>("usersDataTable", columns, dataProvider,
                10);
        add(usersProfileDataTable);

    }

    private class SubmitButton extends AjaxButton {
        private static final long serialVersionUID = 1L;

        public SubmitButton(String id, Form<EaterProfile> form) {
            super(id, form);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (StringUtils.isNotBlank(form.getModelObject().getId())) {
                tag.getAttributes().put("value", new StringResourceModel("button.create", this, null).getString());
            } else {
                tag.getAttributes().put("value", new StringResourceModel("button.update", this, null).getString());
            }
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            try {
                EaterProfile user = (EaterProfile) form.getModelObject();
                user = userProfileService.save(user);
                getFeedbackPanel().info(new StringResourceModel("info.userprofileupdated", this, null).getString());
                refreshDataTable();
                target.addComponent(usersProfileDataTable);
                target.addComponent(form);
            } catch (YoueatException e) {
                getFeedbackPanel().error("ERROR" + e.getMessage());
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
        dataProvider.fetchResults();
    }

    public final Form<EaterProfile> getForm() {
        return form;
    }

    public final EaterProfile getUserProfile() {
        return userProfile;
    }

    public final void setUserProfile(EaterProfile userProfile) {
        this.userProfile = userProfile;
    }

    public final EaterProfileService getUsersProfileServices() {
        return userProfileService;
    }

    public final AjaxFallbackDefaultDataTable<EaterProfile> getUsersProfileDataTable() {
        return usersProfileDataTable;
    }

}
