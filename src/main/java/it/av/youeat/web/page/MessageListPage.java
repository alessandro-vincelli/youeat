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
import it.av.youeat.util.TemplateUtil;
import it.av.youeat.web.components.ImagesAvatar;
import it.av.youeat.web.util.EaterUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Check friends list, Confirm and remove friends, send a message to a friend
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@RequireHttps
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class MessageListPage extends BasePage {

    @SpringBean
    private DialogService dialogService;
    @SpringBean
    private MessageService messageService;
    @SpringBean
    private TemplateUtil templateUtil;
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

            @Override
            protected void populateItem(final ListItem<Message> item) {
                // if the dialog contains unread message, use a different CSS style
                if (!(item.getModelObject().getSender().equals(getLoggedInUser()))
                        && item.getModelObject().getReadTime() == null) {
                    item.add(new AttributeAppender("class", new Model<String>("rowMessageUnread"), " "));
                }
                Eater sender = item.getModelObject().getSender();
                Eater recipient = item.getModelObject().getDialog().checkCounterpart(sender);
                item.add(ImagesAvatar.getAvatar("avatar", sender, this.getPage(), true));
                item.add(new BookmarkablePageLink("linkToUser", EaterViewPage.class, EaterUtil.createParamsForEater(sender)).add(new Label(Message.SENDER_FIELD)));
                BookmarkablePageLink recipientLink = new BookmarkablePageLink("linkToRecipientUser", EaterViewPage.class, EaterUtil.createParamsForEater(recipient));
                recipientLink.add(new Label("recipient", recipient.toString()));
                //visible only on Sent page
                recipientLink.setVisible(!inBox);
                item.add(recipientLink);
                item.add(new Label(Message.SENTTIME_FIELD));
                item.add(new OpenMessage("openMessageTitle", new Model<Message>(item.getModelObject()), item).add(new Label(Message.TITLE_FIELD)));
                String messageBodyShort = StringUtils.abbreviate(templateUtil.resolveTemplateEater(item.getModelObject(), false, null, getWebPage()), 150);
                item.add(new OpenMessage("openMessage", new Model<Message>(item.getModelObject()), item).add(new Label(
                        Message.BODY_FIELD, messageBodyShort)));
                item.add(new AjaxFallbackLink<Message>("remove", new Model<Message>(item.getModelObject())) {

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
        
        add(new BookmarkablePageLink("goSearchFriendPage", SearchFriendPage.class));
        
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
        long numberSentMsgs = dialogService.countCreatedDialogs(getLoggedInUser());
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
            dialogs = dialogService.getDialogs(getLoggedInUser(), true);
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