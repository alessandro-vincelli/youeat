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
import it.av.youeat.service.system.MailService;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements the operation on {@link Dialog}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class DialogServiceHibernate extends ApplicationServiceHibernate<Dialog> implements DialogService {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MailService mailService;

    /**
     * {@inheritDoc}
     */
    @Override
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
        Criterion critBySender = Restrictions.eq(Dialog.SENDER_FIELD, eater);
        Order orderBYDate = Order.asc(Dialog.CREATION_TIME_FIELD);
        return findByCriteria(orderBYDate, critBySender);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Dialog> getDialogs(Eater eater) {
        Criterion critByReceiver = Restrictions.eq(Dialog.RECEIVER_FIELD, eater);
        Criterion critBySender = Restrictions.eq(Dialog.SENDER_FIELD, eater);
        Disjunction or = Restrictions.disjunction();
        or.add(critBySender);
        or.add(critByReceiver);
        Order orderBYDate = Order.desc(Dialog.CREATION_TIME_FIELD);
        List<Dialog> results = findByCriteria(orderBYDate, or);
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getSender().equals(eater) && results.get(i).getMessages().size() == 1) {
                results.remove(i);
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public Dialog startNewDialog(Eater sender, Eater recipient, Message message) {
        message.setSentTime(DateUtil.getTimestamp());
        Dialog dialog = new Dialog();
        dialog.setCreationTime(DateUtil.getTimestamp());
        dialog.setReceiver(recipient);
        dialog.setSender(sender);
        dialog.getMessages().add(message);
        message.setDialog(dialog);
        dialog = save(dialog);
        sendNotification(recipient, message);
        return dialog;
    }

    private void sendNotification(Eater eater, Message message) {
        mailService.sendMessageReceivedNotification(eater, message);
    }
}