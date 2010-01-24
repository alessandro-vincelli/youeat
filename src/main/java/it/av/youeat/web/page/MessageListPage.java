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
import it.av.youeat.ocm.model.Dialog;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.DialogService;
import it.av.youeat.service.MessageService;
import it.av.youeat.web.components.ImagesAvatar;
import it.av.youeat.web.components.OpenFriendPageButton;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
public class MessageListPage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean
    private DialogService dialogService;
    @SpringBean
    private MessageService messageService;
    private PropertyListView<Message> messageList;

    /**
     * default true, used to get correct list of dialog/messages
     */
    private boolean inBox = true;

    public MessageListPage() {
        super();
        add(getFeedbackPanel());
        final Label noYetMessages = new Label("noYetMessages", getString("noMessages")) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(getLastMessages().size() == 0);
            }
        };
        noYetMessages.setOutputMarkupId(true);
        noYetMessages.setOutputMarkupPlaceholderTag(true);
        add(noYetMessages);

        final WebMarkupContainer messagesListContainer = new WebMarkupContainer("messagesListContainer");
        messagesListContainer.setOutputMarkupPlaceholderTag(true);
        add(messagesListContainer);
        messageList = new PropertyListView<Message>("messagesList", new MessagesModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<Message> item) {
                // if the dialog contains unread message, use a different CSS style
                if (!(item.getModelObject().getSender().equals(getLoggedInUser()))
                        && item.getModelObject().getReadTime() == null) {
                    item.add(new AttributeAppender("class", new Model<String>("rowMessageUnread"), " "));
                }
                Eater sender = item.getModelObject().getSender();
                item.add(ImagesAvatar.getAvatar("avatar", sender, this.getPage(), true));
                item.add(new OpenFriendPageButton("linkToUser", sender).add(new Label(Message.SENDER_FIELD)));
                item.add(new Label(Message.SENTTIME_FIELD));
                item.add(new Label(Message.TITLE_FIELD));
                item.add(new OpenMessage("openMessage", new Model<Message>(item.getModelObject()), item).add(new Label(
                        Message.BODY_FIELD)));
                item.add(new AjaxFallbackLink<Message>("remove", new Model<Message>(item.getModelObject())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        try {
                            ((MessageListPage) getPage()).dialogService.delete(getModelObject().getDialog(),
                                    getLoggedInUser());
                            noYetMessages.setVisible(getLastMessages().size() == 0);
                            info(getString("info.userRelationRemoved"));
                        } catch (YoueatException e) {
                            error(new StringResourceModel("genericErrorMessage", this, null).getString());
                        }
                        if (target != null) {
                            target.addComponent(getFeedbackPanel());
                            target.addComponent((noYetMessages));
                            target.addComponent((messagesListContainer));
                            target.addComponent(((MessageListPage) target.getPage()).getFeedbackPanel());
                        }
                    }
                });
                item.add(new OpenMessage("open", new Model<Message>(item.getModelObject()), item));
            }
        };
        messagesListContainer.add(messageList);
        add(new AjaxFallbackLink<String>("goSearchFriendPage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SearchFriendPage.class);
            }
        });
        
        long numberUnreadMsgs = messageService.countUnreadMessages(getLoggedInUser());
        final WebMarkupContainer separator = new WebMarkupContainer("separator");
        separator.setVisible(numberUnreadMsgs > 0);

        final Label unreadMsgs = new Label("unreadMessages", new Model<Long>(numberUnreadMsgs));
        unreadMsgs.setOutputMarkupPlaceholderTag(true);
        unreadMsgs.setVisible(numberUnreadMsgs > 0);
        
        AjaxFallbackLink<String> inboxButton = new AjaxFallbackLink<String>("inbox") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                inBox = true;
                noYetMessages.setVisible(getLastMessages().size() == 0);
                if (target != null) {
                    target.addComponent((messagesListContainer));
                    target.addComponent((noYetMessages));
                }
            }
        };
        add(inboxButton);
        add(new Label("numberMessages", Integer.toString(messageList.getModel().getObject().size())));
        add(unreadMsgs);
        add(separator);
        add(new AjaxFallbackLink<String>("sentitems") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                inBox = false;
                noYetMessages.setVisible(getLastMessages().size() == 0);
                if (target != null) {
                    target.addComponent((messagesListContainer));
                    target.addComponent((noYetMessages));
                }
            }
        });
        long numberSentMsgs = messageService.countSentMessages(getLoggedInUser());
        add(new Label("numberSentMessages", Long.toString(numberSentMsgs)));
    }

    /**
     * get the last messages the logged user
     * 
     * @param inBox true for inbox messages, false for sent messages
     * @return list of messages
     */
    private List<Message> getLastMessages() {
        List<Dialog> dialogs;
        if (inBox) {
            dialogs = dialogService.getDialogs(getLoggedInUser());
        } else {
            dialogs = dialogService.getCreatedDialogs(getLoggedInUser());
        }
        List<Message> messages = new ArrayList<Message>(dialogs.size());
        for (Dialog dialog : dialogs) {
            messages.add(dialog.getMessages().last());
        }
        return messages;
    }

    private final class OpenMessage extends AjaxFallbackLink<Message> {
        private final ListItem<Message> item;

        private OpenMessage(String id, IModel<Message> model, ListItem<Message> item) {
            super(id, model);
            this.item = item;
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            PageParameters pp = new PageParameters();
            pp.add(YoueatHttpParams.DIALOG_ID, item.getModelObject().getDialog().getId());
            setResponsePage(MessagePage.class, pp);
        }
    }

    private class MessagesModel extends LoadableDetachableModel<List<Message>> {
        public MessagesModel() {
            super();
        }

        public MessagesModel(List<Message> messages) {
            super(messages);
        }

        @Override
        protected List<Message> load() {
            return getLastMessages();
        }
    }
}