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
package it.av.youeat.service.impl;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Dialog;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.DialogService;
import it.av.youeat.service.MessageService;
import it.av.youeat.service.SocialService;
import it.av.youeat.service.system.MailService;
import it.av.youeat.util.TemplateUtil;
import it.av.youeat.web.Locales;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the operation on {@link Dialog}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Repository
@Transactional(readOnly = true)
public class DialogServiceHibernate extends ApplicationServiceHibernate<Dialog> implements DialogService {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MailService mailService;
    @Autowired
    private SocialService socialService;
    @Autowired
    private MessageSource messageSource;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(Dialog dialog, Eater eater) {
        if (dialog.getReceiver().equals(eater)) {
            dialog.setDeletedFromReceiver(true);
        } else if (dialog.getSender().equals(eater)) {
            dialog.setDeletedFromSender(true);
        } else {
            throw new YoueatException("Given eater is not an owner of the given dialog");
        }
        super.save(dialog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Dialog> getCreatedDialogs(Eater eater) {
        Conjunction senderFilter = Restrictions.conjunction();
        Criterion critBySender1 = Restrictions.eq(Dialog.SENDER_FIELD, eater);
        Criterion critBySender2 = Restrictions.eq(Dialog.DELETED_FROM_SENDER_FIELD, false);
        senderFilter.add(critBySender1);
        senderFilter.add(critBySender2);
        Order orderBYDate = Order.desc(Dialog.CREATION_TIME_FIELD);
        return findByCriteria(orderBYDate, senderFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countCreatedDialogs(Eater eater) {
        Criteria criteria = getHibernateSession().createCriteria(getPersistentClass());
        Conjunction senderFilter = Restrictions.conjunction();
        Criterion critBySender1 = Restrictions.eq(Dialog.SENDER_FIELD, eater);
        Criterion critBySender2 = Restrictions.eq(Dialog.DELETED_FROM_SENDER_FIELD, false);
        senderFilter.add(critBySender1);
        senderFilter.add(critBySender2);
        criteria.add(senderFilter);
        criteria.setProjection(Projections.rowCount());
        return (Integer) criteria.list().get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Dialog> getDialogs(Eater eater, boolean excludeSingleMessage) {
        Conjunction receiverFilter = Restrictions.conjunction();
        Criterion critByReceiver1 = Restrictions.eq(Dialog.RECEIVER_FIELD, eater);
        Criterion critByReceiver2 = Restrictions.eq(Dialog.DELETED_FROM_RECEIVER_FIELD, false);
        receiverFilter.add(critByReceiver1);
        receiverFilter.add(critByReceiver2);

        Conjunction senderFilter = Restrictions.conjunction();
        Criterion critBySender1 = Restrictions.eq(Dialog.SENDER_FIELD, eater);
        Criterion critBySender2 = Restrictions.eq(Dialog.DELETED_FROM_SENDER_FIELD, false);
        senderFilter.add(critBySender1);
        senderFilter.add(critBySender2);

        Disjunction or = Restrictions.disjunction();
        or.add(receiverFilter);
        or.add(senderFilter);
        Order orderBYDate = Order.desc(Dialog.CREATION_TIME_FIELD);
        List<Dialog> results = findByCriteria(orderBYDate, or);
        if (excludeSingleMessage) {
            for (int i = 0; i < results.size(); i++) {
                if (results.get(i).getSender().equals(eater) && results.get(i).getMessages().size() == 1) {
                    results.remove(i);
                }
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Dialog readDiscussion(String dialogId, Eater eater) {
        Dialog dialog = getByID(dialogId);
        for (Message msg : dialog.getMessages()) {
            if (!msg.getSender().equals(eater)) {
                messageService.markMessageAsRead(msg);
            }
        }
        return dialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Dialog reply(Message message, Dialog dialog, Eater recipient) {
        message.setSentTime(DateUtil.getTimestamp());
        message.setDialog(dialog);
        dialog.getMessages().add(message);
        Dialog savedDialog = save(dialog);
        sendNotification(recipient, message);
        return savedDialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Dialog startNewDialog(Eater sender, Eater recipient, Message message) {
        return startNewDialog(sender, recipient, message, true);
    }

    /**
     * Create a new dialog containing the new message.
     * 
     * @param sender the creator of the dialog
     * @param recipient the receiver of the first message of the dialog
     * @param message to send
     * @param sendNotifcation true to send email notification to the recipient
     * @return the created dialog
     */
    @Transactional
    Dialog startNewDialog(Eater sender, Eater recipient, Message message, boolean sendNotifcation) {
        message.setSentTime(DateUtil.getTimestamp());
        message.setSender(sender);
        Dialog dialog = new Dialog();
        dialog.setCreationTime(DateUtil.getTimestamp());
        dialog.setReceiver(recipient);
        dialog.setSender(sender);
        dialog.getMessages().add(message);
        message.setDialog(dialog);
        dialog = save(dialog);
        if (sendNotifcation) {
            sendNotification(recipient, message);
        }
        return dialog;
    }

    private void sendNotification(Eater recipient, Message message) {
        if (recipient.isSocialNetworkEater()) {
            socialService.sendMessageReceivedNotification(recipient, message);
        } else {
            mailService.sendMessageReceivedNotification(recipient, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeByEater(Eater eater) {
        List<Dialog> dialogs = getDialogs(eater, false);
        for (Dialog dialog : dialogs) {
            this.remove(dialog);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void remove(Dialog dialog) {
        messageService.remove(dialog.getMessages());
        super.remove(dialog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void sendFriendSuggestions(Eater sender, Eater recipient, Set<Eater> friendsToSuggest) {
        Assert.assertNotNull(friendsToSuggest);
        if (!friendsToSuggest.isEmpty()) {
            Message message = createMessageForFriendSuggestion(sender, friendsToSuggest, recipient);
            startNewDialog(sender, recipient, message, false);
            //send a custom mail notification
            sendFriendSuggestionNotification(sender, friendsToSuggest, recipient);
        }
    }

    /**
     * Created an i18n message containing a list of a new friend to suggest
     * 
     * @param sender the sender of the suggestion
     * @param friendsToSuggest the friend to suggest
     * @param recipient the recipient of the suggestion
     * @return a message containing the friends to suggest
     */
    Message createMessageForFriendSuggestion(Eater sender, Set<Eater> friendsToSuggest, Eater recipient) {
        Locale locale = Locales.getSupportedLocale(recipient.getLanguage().getLanguage());
        if (friendsToSuggest.size() == 1) {
            Eater eaterToSuggest = friendsToSuggest.iterator().next();
            // params [1=sender]
            Object[] params = { sender.getFirstname() };
            String title = messageSource.getMessage("suggestNewFriend.title", params, locale);

            StringBuffer textBody = new StringBuffer();
            // params [1=sender], [2=newFriend]
            Object[] paramsBody = { TemplateUtil.templateEater(sender), TemplateUtil.templateEater(eaterToSuggest) };
            textBody.append(messageSource.getMessage("suggestNewFriend.body", paramsBody, locale));
            return new Message(title, textBody.toString());
        } else {
            // params [1=sender]
            Object[] params = { sender.getFirstname() };
            String title = messageSource.getMessage("suggestNewFriend.titleMultipleUsers", params, locale);
            StringBuffer friendsList = new StringBuffer();
            for (Iterator<Eater> iterator = friendsToSuggest.iterator(); iterator.hasNext();) {
                Eater eaterToSuggest = (Eater) iterator.next();
                friendsList.append(TemplateUtil.templateEater(eaterToSuggest));
                if (iterator.hasNext()) {
                    friendsList.append(", ");
                }
            }
            StringBuffer textBody = new StringBuffer();
            // params [1=sender], [2=newFriendListCommaSepared]
            Object[] params2 = { TemplateUtil.templateEater(sender), friendsList.toString() };
            textBody.append(messageSource.getMessage("suggestNewFriend.bodyMultipleUsers", params2, locale));
            return new Message(title, textBody.toString());
        }
    }
    

    /**
     * Send a notification about the friend suggestion
     *  
     * @param sender the sender of the suggestion
     * @param friendsToSuggest list of friend to suggest
     * @param recipient the recipient of the suggestions and the recipient of the email notification
     */
    void sendFriendSuggestionNotification(Eater sender, Set<Eater> friendsToSuggest, Eater recipient) {
        if (recipient.isSocialNetworkEater()) {
            // TODO
            //socialService.sendMessageReceivedNotification(recipient, message);
        } else {
            mailService.sendFriendSuggestionNotification(sender, friendsToSuggest, recipient);
        }
    }

}