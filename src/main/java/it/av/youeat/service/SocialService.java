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

import it.av.youeat.ocm.model.ActivityRistorante;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Operations and services on social networks
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Service
@Transactional
public interface SocialService {

    /**
     * <b>TODO</b>
     * Sends a notification to the notify the receiver of a new {@link Message}
     * 
     * @param sender
     * @param recipient
     * @param message the message to be notified
     */
    @Transactional
    void sendMessageReceivedNotification(Eater recipient, Message message);

    /**
     * Publish an activity on the facebook wall of YouEat
     * 
     * @param activityRistorante the activity to be published
     */
    @Transactional
    void publishRistoActivity(ActivityRistorante activityRistorante);
}