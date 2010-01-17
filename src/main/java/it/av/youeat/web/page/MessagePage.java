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
import it.av.youeat.web.components.ImagesAvatar;
import it.av.youeat.web.components.OpenFriendPageButton;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Check friends list, Confirm and remove friends, send a message to a friend
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class MessagePage extends BasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean
    private DialogService dialogService;
    private PropertyListView<Message> messageList;
    private List<Message> allMessages;
    private Dialog dialog;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public MessagePage(PageParameters parameters) throws YoueatException {
        super();
        final String dialogId = parameters.getString(YoueatHttpParams.PARAM_DIALOG_ID, "");
        if (dialogId == "") {
            throw new YoueatException("dialog id is empty");
        }

        add(getFeedbackPanel());

        dialog = dialogService.readDiscussion(dialogId, getLoggedInUser());
        allMessages = getMessagesInTheDialog();
        final WebMarkupContainer messageListContainer = new WebMarkupContainer("messagesListContainer");
        messageListContainer.setOutputMarkupId(true);
        add(messageListContainer);
        messageList = new PropertyListView<Message>("messagesList", allMessages) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<Message> item) {
                item.add(new OpenFriendPageButton("linkToUser", item.getModelObject().getSender()).add(new Label(
                        Message.SENDER_FIELD)));
                item.add(new Label(Message.SENTTIME_FIELD));
                item.add(new Label(Message.BODY_FIELD));
                item.add(new Label(Message.TITLE_FIELD));
                item.add(ImagesAvatar.getAvatar("avatar", item.getModelObject().getSender(), this.getPage(), true));
            }
        };
        messageListContainer.add(messageList);

        final Form<Message> sendMessageForm = new Form<Message>("sendMessageForm", new CompoundPropertyModel<Message>(
                getNewMessage()));
        sendMessageForm.setOutputMarkupId(true);
        add(sendMessageForm);
        sendMessageForm.add(new TextArea<String>("body").setRequired(true).add(
                StringValidator.maximumLength(Message.BODY_MAX_LENGTH)));
        sendMessageForm.add(new AjaxFallbackButton("submit", sendMessageForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Message msgToSend = (Message) form.getModelObject();
                dialogService.reply(msgToSend, dialog, getCounterpart(dialog));
                dialog = dialogService.readDiscussion(dialogId, getLoggedInUser());
                allMessages = getMessagesInTheDialog();
                messageList.setModelObject(allMessages);
                sendMessageForm.setModelObject(getNewMessage());
                if (target != null) {
                    target.addComponent(getFeedbackPanel());
                    target.addComponent(sendMessageForm);
                    target.addComponent(messageListContainer);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                // for the moment I don't want show the error message
                target.addComponent(getFeedbackPanel());
            }
        });

        add(new AjaxFallbackLink<String>("goSearchFriendPage") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(SearchFriendPage.class);
            }
        });
    }

    private Eater getCounterpart(Dialog dialog) {
        if (dialog.getReceiver().equals(getLoggedInUser())) {
            return dialog.getSender();
        } else {
            return dialog.getReceiver();
        }
    }

    private Message getNewMessage() {
        Message msg = new Message();
        msg.setSender(getLoggedInUser());
        return msg;
    }

    private List<Message> getMessagesInTheDialog() {
        Message[] messa = new Message[dialog.getMessages().size()];
        return Arrays.asList(dialog.getMessages().toArray(messa));
    }
}