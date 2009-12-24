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

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.EaterRelation;
import it.av.eatt.service.EaterRelationService;
import it.av.eatt.web.commons.ImagesCommons;
import it.av.eatt.web.commons.YoueatHttpParams;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * See, Confirm and remove friends.
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class FriendsPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean
    private EaterRelationService userRelationService;
    private PropertyListView<EaterRelation> friendsList;
    private List<EaterRelation> allRelations;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws JackWicketException
     */
    public FriendsPage() throws JackWicketException {
        super();
        allRelations = userRelationService.getAllRelations(getLoggedInUser());
        final Label noYetFriends = new Label("noYetFriends", getString("noYetFriends")) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(allRelations.size() == 0);
            }
        };
        noYetFriends.setOutputMarkupId(true);
        noYetFriends.setOutputMarkupPlaceholderTag(true);
        add(noYetFriends);
        final WebMarkupContainer friendsListContainer = new WebMarkupContainer("friendsListContainer");
        friendsListContainer.setOutputMarkupId(true);
        add(friendsListContainer);
        friendsList = new PropertyListView<EaterRelation>("friendsList", allRelations) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<EaterRelation> item) {
                boolean isPendingFriendRequest = item.getModelObject().getStatus().equals(EaterRelation.STATUS_PENDING)
                        && item.getModelObject().getToUser().equals(getLoggedInUser());
                AjaxFallbackLink<String> linkToUser = new AjaxFallbackLink<String>("linkToUser") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        PageParameters pp = new PageParameters(YoueatHttpParams.USER_ID + "="
                                + item.getModelObject().getToUser().getId());
                        setResponsePage(UserViewPage.class, pp);
                    }
                };
                item.add(linkToUser);
                Eater eaterToshow = item.getModelObject().getToUser();
                if (getLoggedInUser().equals(item.getModelObject().getToUser())) {
                    eaterToshow = item.getModelObject().getFromUser();
                }
                linkToUser.add(new Label(EaterRelation.TO_USER + ".firstname", eaterToshow.getFirstname()));
                linkToUser.add(new Label(EaterRelation.TO_USER + ".lastname", eaterToshow.getLastname()));
                
                item.add(ImagesCommons.getAvatar("avatar", eaterToshow, this.getPage(), true));
                item.add(new Label(EaterRelation.TYPE));
                item.add(new Label(EaterRelation.STATUS));
                // item.add(new Label(EaterRelation.TO_USER + ".userRelation"));
                item.add(new AjaxLink<EaterRelation>("remove", new Model<EaterRelation>(item.getModelObject())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((FriendsPage) getPage()).userRelationService.remove(getModelObject());
                            allRelations = userRelationService.getAllRelations(getLoggedInUser());
                            ((FriendsPage) target.getPage()).friendsList.setModelObject(allRelations);
                            noYetFriends.setVisible(allRelations.size() == 0);
                            target.addComponent((noYetFriends));
                            target.addComponent((friendsListContainer));
                            // info(new StringResourceModel("info.userRelationRemoved", this, null).getString());
                        } catch (JackWicketException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        target.addComponent(((FriendsPage) target.getPage()).getFeedbackPanel());
                    }
                });
                item.add(new AjaxLink<EaterRelation>("acceptFriend", new Model<EaterRelation>(item.getModelObject())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((FriendsPage) getPage()).userRelationService.performFriendRequestConfirm(getModelObject());
                            allRelations = userRelationService.getAllRelations(getLoggedInUser());
                            ((FriendsPage) target.getPage()).friendsList.setModelObject(allRelations);
                            noYetFriends.setVisible(allRelations.size() == 0);
                            target.addComponent((noYetFriends));
                            target.addComponent((friendsListContainer));
                            // info(new StringResourceModel("info.userRelationRemoved", this, null).getString());
                        } catch (JackWicketException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        target.addComponent(((FriendsPage) target.getPage()).getFeedbackPanel());
                    }
                }.setVisible(isPendingFriendRequest));

                item.add(new AjaxLink<EaterRelation>("ignoreFriendRequest", new Model<EaterRelation>(item
                        .getModelObject())) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((FriendsPage) getPage()).userRelationService.performFriendRequestIgnore(getModelObject());
                            allRelations = userRelationService.getAllRelations(getLoggedInUser());
                            ((FriendsPage) target.getPage()).friendsList.setModelObject(allRelations);
                            noYetFriends.setVisible(allRelations.size() == 0);
                            target.addComponent((noYetFriends));
                            target.addComponent((friendsListContainer));
                            // info(new StringResourceModel("info.userRelationRemoved", this, null).getString());
                        } catch (JackWicketException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        target.addComponent(((FriendsPage) target.getPage()).getFeedbackPanel());
                    }
                }.setVisible(isPendingFriendRequest));
            }
        };
        friendsListContainer.add(friendsList);
        add(new AjaxLink<String>("goSearchFriendPage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SearchFriendPage.class);
            }
        });
    }
}