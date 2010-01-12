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
package it.av.eatt.service;

import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.Message;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operations on {@Link Message}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Service
@Transactional
public interface MessageService {

    /**
     * Send a message, store two message in the DB.<br>
     * One of the message is saved has <i>isReceived=true</i>.
     * 
     * @param message to send
     * @return the sent message
     */
    @Transactional
    Message send(Message message);

    /**
     * Return the activity on the given date
     * 
     * @param date
     * @return activities on the given date
     */
    @Transactional(readOnly = true)
    List<Message> findByDate(Date date);

    /**
     * Find messages received from the given user
     * 
     * @param eater
     * @return message for the given user
     */
    @Transactional(readOnly = true)
    List<Message> findReceived(Eater eater);
    
    /**
     * Find deleted messages received from the given user
     * 
     * @param eater
     * @return message for the given user
     */
    @Transactional(readOnly = true)
    List<Message> findReceivedDeleted(Eater eater);
    
    /**
     * Find messages sent by the given user
     * 
     * @param eater
     * @return message for the given user
     */
    @Transactional(readOnly = true)
    List<Message> findSent(Eater eater);
    
    /**
     * Find deleted messages sent by the given user
     * 
     * @param eater
     * @return message for the given user
     */
    @Transactional(readOnly = true)
    List<Message> findSentDeleted(Eater eater);

    /**
     * Remove logically the Message
     */
    @Transactional
    void purge(Message message);
    
    /**
     * Remove the message from the database
     */
    @Transactional
    void delete(Message message);

}