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

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;
import it.av.youeat.ocm.util.DateUtil;
import it.av.youeat.service.MessageService;

import javax.persistence.Query;

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
    public Message markMessageAsRead(Message msg) {
        if (msg.getReadTime() == null) {
            msg.setReadTime(DateUtil.getTimestamp());
            System.out.println(msg.getId() + "reading");
            return save(msg);
        }
        return msg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countUnreadMessages(Eater eater) {
        Query query = getJpaTemplate()
                .getEntityManager()
                .createQuery(
                        "select count(msgs) from Message as msgs inner join msgs.dialog as dia where (dia.receiver = :diaReceiver or dia.sender = :diaSender) and msgs.readTime = null and msgs.sender != :sender");
        query.setParameter("diaReceiver", eater);
        query.setParameter("diaSender", eater);
        query.setParameter("sender", eater);
        return (Long) query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countMessages(Eater eater) {
        Query query = getJpaTemplate()
                .getEntityManager()
                .createQuery(
                        "select count(msgs) from Message as msgs inner join msgs.dialog as dia where dia.receiver = :receiver or dia.sender = :sender");
        query.setParameter("sender", eater);
        query.setParameter("receiver", eater);
        return (Long) query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message getMessage(Message message) {
        return getByID(message.getId());
    }

}