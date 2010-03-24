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
import it.av.youeat.ocm.model.Message;
import it.av.youeat.service.DialogService;
import it.av.youeat.util.TemplateUtil;
import it.av.youeat.web.components.ImagesAvatar;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Show all the messages of a dialog
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@AuthorizeInstantiation( { "USER", "ADMIN" })
public class MessagePage extends BasePage {

    @SpringBean
    private DialogService dialogService;
    @SpringBean
    private TemplateUtil templateUtil;
    private PropertyListView<Message> messageList;
    private Dialog dialog;

    /**
     * Constructor that is invoked when page is invoked without a session.
     * 
     * @throws YoueatException
     */
    public MessagePage(PageParameters parameters) throws YoueatException {
        super();
        final String dialogId = parameters.getString(YoueatHttpParams.DIALOG_ID, "");
        if (StringUtils.isBlank(dialogId)) {
            throw new RestartResponseAtInterceptPageException(getApplication().getHomePage());
        }

        add(getFeedbackPanel());

        dialog = dialogService.readDiscussion(dialogId, getLoggedInUser());

        final WebMarkupContainer messageListContainer = new WebMarkupContainer("messagesListContainer");
        messageListContainer.setOutputMarkupId(true);
        add(messageListContainer);
        messageList = new PropertyListView<Message>("messagesList", new MessagesModel()) {

            @Override
            protected void populateItem(final ListItem<Message> item) {
                item.add(new BookmarkablePageLink("linkToUser", EaterViewPage.class, new PageParameters(
                        YoueatHttpParams.YOUEAT_ID + "=" + item.getModelObject().getSender().getId())).add(new Label(
                        Message.SENDER_FIELD)));
                item.add(new Label(Message.SENTTIME_FIELD));
                String body = templateUtil.resolveTemplateEater(item.getModelObject(), true, null);
                item.add(new Label(Message.BODY_FIELD, body).setEscapeModelStrings(false));
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
                dialogService.reply(msgToSend, dialog, dialog.checkCounterpart(getLoggedInUser()));
                dialog = dialogService.readDiscussion(dialogId, getLoggedInUser());
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

        add(new BookmarkablePageLink("goSearchFriendPage", SearchFriendPage.class));
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

    private class MessagesModel extends LoadableDetachableModel<List<Message>> {

        public MessagesModel() {
            super();
        }

        public MessagesModel(List<Message> comments) {
            super(comments);
        }

        @Override
        protected List<Message> load() {
            return getMessagesInTheDialog();
        }

    }
}