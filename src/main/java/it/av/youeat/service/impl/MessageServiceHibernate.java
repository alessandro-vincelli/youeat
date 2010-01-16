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
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.MessageService;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;

/**
 * Implements operations on {@link Message}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class MessageServiceHibernate extends ApplicationServiceHibernate<Message> implements MessageService {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> findByDate(Date date) {
        Criterion critByDate = Restrictions.eq(Message.SENTTIME_FIELD, date);
        Order orderBYDate = Order.asc(Message.SENTTIME_FIELD);
        return findByCriteria(orderBYDate, critByDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> findReceived(Eater eater) {
        return find(eater, true, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Message> findSent(Eater eater) {
        return find(eater, false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message send(Message message) {
        Assert.notNull(message.getReceiver());
        Assert.notNull(message.getSender());
        Assert.notNull(message.getBody());
        Assert.isTrue(message.getId() == null);
        message.setSentTime(DateUtil.getTimestamp());
        return super.save(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message reply(Message message, Message receivedMessage) {
        message.setReplyto(receivedMessage);
        Message sentMessage = send(message);
        receivedMessage.setReplyfrom(sentMessage);
        save(receivedMessage);
        return sentMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Message message, Eater eater) {
        if (message.getReceiver().equals(eater)) {
            message.setDeletedFromReceiver(true);
        } else if (message.getSender().equals(eater)) {
            message.setDeletedFromSender(true);
        } else {
            throw new YoueatException("Given eater is not an owner of the given message");
        }
        super.save(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purge(Message message) {
        super.remove(message);
    }

    @Override
    public List<Message> findReceivedDeleted(Eater eater) {
        return find(eater, true, true);
    }

    @Override
    public List<Message> findSentDeleted(Eater eater) {
        return find(eater, false, true);
    }

    protected List<Message> find(Eater eater, boolean isReceived, boolean deleted) {
        Criterion critByUser;
        Criterion critIsDeleted = null;
        if (isReceived) {
            critByUser = Restrictions.eq(Message.RECEIVER_FIELD, eater);
        } else {
            critByUser = Restrictions.eq(Message.SENDER_FIELD, eater);
        }
        if (deleted) {
            if (isReceived) {
                critIsDeleted = Restrictions.eq(Message.DELETED_FROM_RECEIVER_FIELD, true);
            } else {
                critIsDeleted = Restrictions.eq(Message.DELETED_FROM_SENDER_FIELD, true);
            }
        }
        if (!deleted) {
            if (isReceived) {
                critIsDeleted = Restrictions.eq(Message.DELETED_FROM_RECEIVER_FIELD, false);
            } else {
                critIsDeleted = Restrictions.eq(Message.DELETED_FROM_SENDER_FIELD, false);
            }
        }
        Order orderBYDate = Order.asc(Message.SENTTIME_FIELD);
        return findByCriteria(orderBYDate, critByUser, critIsDeleted);
    }
}