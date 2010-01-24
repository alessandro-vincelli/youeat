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
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.web.page.SearchFriendPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Start new relation
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class SearchFriendTableActionPanel extends Panel {
    @SpringBean
    private EaterRelationService userRelationService;

    /**
     * @param id component id
     * @param model model for contact
     */
    public SearchFriendTableActionPanel(String id, IModel<Eater> model) {
        super(id, model);
        add(new AjaxFallbackLink<Eater>("addFriend", model) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                SearchFriendPage page = ((SearchFriendPage) getPage());
                try {
                    userRelationService.addFriendRequest(page.getLoggedInUser(), getModelObject());
                    page.refreshDataTable();
                    info(getString("friendRequestSent", getModel()));
                } catch (YoueatException e) {
                    error(getString("genericErrorMessage"));
                }
                if (target != null) {
                    target.addComponent(page.getSearchFriendsContainer());
                    target.addComponent(page.getFeedbackPanel());
                }
            }
        });
        add(new AjaxFallbackLink<Eater>("followUser", model) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                SearchFriendPage page = ((SearchFriendPage) getPage());
                try {
                    userRelationService.addFollowUser(page.getLoggedInUser(), getModelObject());
                    page.refreshDataTable();
                    info(getString("followUserDone", getModel()));
                } catch (YoueatException e) {
                    error(getString("genericErrorMessage"));
                }
                if (target != null) {
                    target.addComponent(page.getSearchFriendsContainer());
                    target.addComponent(page.getFeedbackPanel());
                }

            }
        });
    }
}