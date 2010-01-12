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
import it.av.eatt.service.EaterRelationService;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Start new realations
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SearchFriendTableActionPanel extends Panel {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private EaterRelationService userRelationService;

    /**
     * @param id component id
     * @param model model for contact
     */
    public SearchFriendTableActionPanel(String id, IModel<Eater> model) {
        super(id, model);
        add(new AjaxLink<Eater>("addFriend", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                SearchFriendPage page = ((SearchFriendPage) getPage());
                try {
                    userRelationService.addFriendRequest(page.getLoggedInUser(), getModelObject());
                    page.refreshDataTable();
                    target.addComponent(page.getSearchFriendsContainer());
                    info(new StringResourceModel("friendRequestSent", this, null).getString());
                } catch (YoueatException e) {
                    error(new StringResourceModel("genericErrorMessage", this, null).getString());
                }
                target.addComponent(page.getFeedbackPanel());
            }
        });
        add(new AjaxLink<Eater>("followUser", model) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                SearchFriendPage page = ((SearchFriendPage) getPage());
                try {
                    userRelationService.addFollowUser(page.getLoggedInUser(), getModelObject());
                    page.refreshDataTable();
                    target.addComponent(page.getSearchFriendsContainer());
                    info(new StringResourceModel("followUserDone", this, null).getString());
                } catch (YoueatException e) {
                    error(new StringResourceModel("genericErrorMessage", this, null).getString());
                }
                target.addComponent(page.getFeedbackPanel());
            }
        });
    }
}