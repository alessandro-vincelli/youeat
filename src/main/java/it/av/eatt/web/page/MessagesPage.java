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
import it.av.eatt.ocm.model.Message;
import it.av.eatt.service.MessageService;
import it.av.eatt.web.components.SendMessagePanel;

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
public class MessagesPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean
    private MessageService messageService;
    private PropertyListView<Message> messageList;
    private List<Message> allMessages;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public MessagesPage() throws YoueatException {
        super();
        add(getFeedbackPanel());
        allMessages = messageService.findReceived(getLoggedInUser());
        final Label noYetFriends = new Label("noYetMessages", getString("noYetFriends")) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(allMessages.size() == 0);
            }
        };
        noYetFriends.setOutputMarkupId(true);
        noYetFriends.setOutputMarkupPlaceholderTag(true);
        add(noYetFriends);
        final ModalWindow sendMessageMW = new ModalWindow("sendMessagePanel");
        sendMessageMW.setInitialHeight(200);
        sendMessageMW.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
        sendMessageMW.setCookieName("youeat-sendMessageModalWindow");

        sendMessageMW.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
            public boolean onCloseButtonClicked(AjaxRequestTarget target) {
                // setResult("Modal window 2 - close button");
                return true;
            }
        });

        sendMessageMW.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
            public void onClose(AjaxRequestTarget target) {
            }
        });

        add(sendMessageMW);
        final WebMarkupContainer friendsListContainer = new WebMarkupContainer("messagesListContainer");
        friendsListContainer.setOutputMarkupId(true);
        add(friendsListContainer);
        messageList = new PropertyListView<Message>("messagesList", allMessages) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<Message> item) {
                Eater sender = item.getModelObject().getSender();
                item.add(new Label(Message.SENDER_FIELD));
                item.add(new Label(Message.SENTTIME_FIELD));
                item.add(new Label(Message.BODY_FIELD));
                item.add(new Label(Message.TITLE_FIELD));
                item.add(new AjaxFallbackLink<Message>("remove", new Model<Message>(item.getModelObject())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((MessagesPage) getPage()).messageService.delete(getModelObject(), getLoggedInUser());
                            allMessages = messageService.findReceived(getLoggedInUser());
                            ((MessagesPage) target.getPage()).messageList.setModelObject(allMessages);
                            noYetFriends.setVisible(allMessages.size() == 0);
                            info(getString("info.userRelationRemoved"));
                        } catch (YoueatException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        if (target != null) {
                            target.addComponent(getFeedbackPanel());
                            target.addComponent((noYetFriends));
                            target.addComponent((friendsListContainer));
                            target.addComponent(((MessagesPage) target.getPage()).getFeedbackPanel());
                        }
                    }
                });
                item.add(new AjaxFallbackLink<Message>("reply", new Model<Message>(item.getModelObject())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        sendMessageMW.setContent(new SendMessagePanel(sendMessageMW.getContentId(), sendMessageMW,
                                getLoggedInUser(), getModelObject().getSender()));
                        sendMessageMW.setTitle(getString("mw.sendMessageTitle", new Model<Eater>(getModelObject()
                                .getSender())));

                        sendMessageMW.show(target);
                    }
                });

            }
        };
        friendsListContainer.add(messageList);
        add(new AjaxFallbackLink<String>("goSearchFriendPage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SearchFriendPage.class);
            }
        });

    }
}