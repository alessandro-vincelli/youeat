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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Provides some {@link AjaxLink} to perform operations on the {@link Eater}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class UserTableActionPanel extends Panel {
    private static final long serialVersionUID = 1L;

    /**
     * @param id component id
     * @param model model for contact
     */
    public UserTableActionPanel(String id, IModel<Eater> model) {
        super(id, model);
        add(new AjaxLink<Eater>("select", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                UserManagerPage page = ((UserManagerPage) getPage());
                page.getForm().setModelObject(getModelObject());
                target.addComponent(page.getForm());
            }
        });
        add(new AjaxLink<Eater>("edit", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                Form<Eater> form = ((UserManagerPage) getPage()).getForm();
                form.setModelObject(getModelObject());
                target.addComponent(form);
            }
        });
        add(new AjaxLink<Eater>("remove", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                UserManagerPage page = ((UserManagerPage) getPage());
                String userName = getModelObject().getFirstname() + " " + getModelObject().getLastname();
                try {
                    page.getUsersServices().remove(getModelObject());
                    page.refreshDataTable();
                    target.addComponent(page.getUsersDataTable());
                    page.getFeedbackPanel().info("Eater \"" + userName + "\" removed");
                } catch (YoueatException e) {
                    page.getFeedbackPanel().error(e.getMessage());
                }
                target.addComponent(page.getFeedbackPanel());
            }
        });
    }
}