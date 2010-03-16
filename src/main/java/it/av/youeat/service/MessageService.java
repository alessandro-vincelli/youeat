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
package it.av.youeat.service;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operations on {@Link Message}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Repository
@Transactional(readOnly = true)
public interface MessageService {

    /**
     * Mark a message as read if unread
     * 
     * @param msg the message to mark
     * @return just mrked message
     */
    @Transactional
    Message markMessageAsRead(Message msg);

    /**
     * Count unread messages for the given user not flagged as removed
     * 
     * @param eater
     * @return number of unread messages
     */
    long countUnreadMessages(Eater eater);

    /**
     * Count messages for the given user, sent and received not flagged as removed
     * 
     * @param eater
     * @return number of messages
     */
    long countMessages(Eater eater);

    /**
     * Count messages received by the given user, not flagged as removed
     * 
     * @param eater
     * @return number of messages
     */
    long countIncomingMessages(Eater eater);

    /**
     * Count messages sent by the given user, not flagged as removed
     * 
     * @param eater
     * @return number of messages
     */
    long countSentMessages(Eater eater);

    /**
     * Get a message from the database
     * 
     * @param message message to retrieve
     * @return a message
     */
    Message getMessage(Message message);
    
    /**
     * remove the given messages from the database
     * 
     * @param messages
     */
    void remove(Collection<Message> messages);
}