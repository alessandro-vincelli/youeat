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
import it.av.youeat.ocm.model.ActivityEaterRelation;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.EaterRelation;
import it.av.youeat.service.ActivityRelationService;
import it.av.youeat.service.EaterRelationService;
import it.av.youeat.web.commons.ActivityPaging;
import it.av.youeat.web.components.ActivitiesRelationListView;
import it.av.youeat.web.components.ImagesAvatar;
import it.av.youeat.web.components.OpenFriendPageButton;
import it.av.youeat.web.components.SendMessageButton;
import it.av.youeat.web.components.SendMessageModalWindow;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Check friends list, Confirm and remove friends, send a message to a friend
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class FriendsPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean
    private EaterRelationService eaterRelationService;
    @SpringBean
    private ActivityRelationService activityService;
    private PropertyListView<EaterRelation> friendsList;
    private List<EaterRelation> allRelations;
    private ActivityPaging activityPagingUser = new ActivityPaging(0, 20);
    private List<ActivityEaterRelation> activities;
    private WebMarkupContainer activitiesListContainer;
    private PropertyListView<ActivityEaterRelation> activitiesList;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public FriendsPage() throws YoueatException {
        super();
        add(getFeedbackPanel());
        allRelations = eaterRelationService.getAllRelations(getLoggedInUser());
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
        final ModalWindow sendMessageMW = SendMessageModalWindow.getNewModalWindow("sendMessagePanel");
        add(sendMessageMW);
        final WebMarkupContainer friendsListContainer = new WebMarkupContainer("friendsListContainer");
        friendsListContainer.setOutputMarkupId(true);
        add(friendsListContainer);
        friendsList = new PropertyListView<EaterRelation>("friendsList", allRelations) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<EaterRelation> item) {
                boolean isPendingFriendRequest = item.getModelObject().getStatus().equals(EaterRelation.STATUS_PENDING)
                        && item.getModelObject().getToUser().equals(getLoggedInUser());
                boolean isActiveFriend = item.getModelObject().getStatus().equals(EaterRelation.STATUS_ACTIVE);
                AjaxFallbackLink<String> linkToUser = new OpenFriendPageButton("linkToUser", item.getModelObject()
                        .getToUser());
                item.add(linkToUser);
                final Eater eaterToshow;
                if (getLoggedInUser().equals(item.getModelObject().getToUser())) {
                    eaterToshow = item.getModelObject().getFromUser();
                } else {
                    eaterToshow = item.getModelObject().getToUser();
                }
                linkToUser.add(new Label(EaterRelation.TO_USER + ".firstname", eaterToshow.getFirstname()));
                linkToUser.add(new Label(EaterRelation.TO_USER + ".lastname", eaterToshow.getLastname()));

                item.add(ImagesAvatar.getAvatar("avatar", eaterToshow, this.getPage(), true));
                item.add(new Label(EaterRelation.TYPE));
                item.add(new Label(EaterRelation.STATUS));
                // item.add(new Label(EaterRelation.TO_USER + ".userRelation"));
                item
                        .add(new AjaxFallbackLink<EaterRelation>("remove", new Model<EaterRelation>(item
                                .getModelObject())) {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                try {
                                    ((FriendsPage) getPage()).eaterRelationService.remove(getModelObject());
                                    allRelations = eaterRelationService.getAllRelations(getLoggedInUser());
                                    ((FriendsPage) target.getPage()).friendsList.setModelObject(allRelations);
                                    noYetFriends.setVisible(allRelations.size() == 0);
                                    info(getString("info.userRelationRemoved"));
                                } catch (YoueatException e) {
                                    error(new StringResourceModel("genericErrorMessage", this, null).getString());
                                }
                                if (target != null) {
                                    target.addComponent(getFeedbackPanel());
                                    target.addComponent((noYetFriends));
                                    target.addComponent((friendsListContainer));
                                    target.addComponent(((FriendsPage) target.getPage()).getFeedbackPanel());
                                }
                            }
                        });
                item.add(new AjaxFallbackLink<EaterRelation>("acceptFriend", new Model<EaterRelation>(item
                        .getModelObject())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((FriendsPage) getPage()).eaterRelationService.performFriendRequestConfirm(getModelObject());
                            allRelations = eaterRelationService.getAllRelations(getLoggedInUser());
                            ((FriendsPage) target.getPage()).friendsList.setModelObject(allRelations);
                            noYetFriends.setVisible(allRelations.size() == 0);
                            // info(new StringResourceModel("info.userRelationRemoved", this, null).getString());
                        } catch (YoueatException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        if (target != null) {
                            target.addComponent((noYetFriends));
                            target.addComponent((friendsListContainer));
                            target.addComponent(((FriendsPage) target.getPage()).getFeedbackPanel());
                        }
                    }
                }.setVisible(isPendingFriendRequest));

                item.add(new AjaxFallbackLink<EaterRelation>("ignoreFriendRequest", new Model<EaterRelation>(item
                        .getModelObject())) {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((FriendsPage) getPage()).eaterRelationService.performFriendRequestIgnore(getModelObject());
                            allRelations = eaterRelationService.getAllRelations(getLoggedInUser());
                            ((FriendsPage) target.getPage()).friendsList.setModelObject(allRelations);
                            noYetFriends.setVisible(allRelations.size() == 0);
                            // info(new StringResourceModel("info.userRelationRemoved", this, null).getString());
                        } catch (YoueatException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        if (target != null) {
                            target.addComponent((noYetFriends));
                            target.addComponent((friendsListContainer));
                            target.addComponent(((FriendsPage) target.getPage()).getFeedbackPanel());
                        }
                    }
                }.setVisible(isPendingFriendRequest));
                item.add(new SendMessageButton("sendMessage", getLoggedInUser(), eaterToshow, sendMessageMW)
                        .setVisible(isActiveFriend));

            }
        };
        friendsListContainer.add(friendsList);
        add(new AjaxFallbackLink<String>("goSearchFriendPage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SearchFriendPage.class);
            }
        });

        // Activities
        try {
            activities = activityService.findByEaterFriendAndEater(getLoggedInUser(), activityPagingUser
                    .getFirstResult(), activityPagingUser.getMaxResults());
        } catch (YoueatException e) {
            activities = new ArrayList<ActivityEaterRelation>();
            error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
        }

        activitiesListContainer = new WebMarkupContainer("activitiesListContainer");
        activitiesListContainer.setOutputMarkupId(true);
        add(activitiesListContainer);
        activitiesList = new ActivitiesRelationListView("activitiesList", activities);
        activitiesList.setOutputMarkupId(true);
        activitiesListContainer.add(activitiesList);
        AjaxFallbackLink<String> moreActivitiesLink = new AjaxFallbackLink<String>("moreActivitiesLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                activityPagingUser.addNewPage();
                try {
                    activities.addAll(activityService.findByEaterFriendAndEater(getLoggedInUser(), activityPagingUser
                            .getFirstResult(), activityPagingUser.getMaxResults()));
                    if (target != null) {
                        target.addComponent(activitiesListContainer);
                    }
                } catch (YoueatException e) {
                    error(new StringResourceModel("error.errorGettingListActivities", this, null).getString());
                }
            }
        };
        activitiesListContainer.add(moreActivitiesLink);
    }
}