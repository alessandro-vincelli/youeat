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

import it.av.youeat.ocm.model.Dialog;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Offer operation to create a dialog between two users
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Service
@Transactional
public interface DialogService {

    /**
     * Create a new dialog containing the new message.
     * 
     * @param sender the creator of the dialog
     * @param recipient the receiver of the first message of the dialog 
     * @param message to send
     * @return the created dialog
     */
    @Transactional
    Dialog startNewDialog(Eater sender, Eater recipient, Message message);

    /**
     * Reply wit a message in the dialog.
     * 
     * @param message message to send
     * @param dialog the dialog for the message
     * @return the sent message
     */
    @Transactional
    Dialog reply(Message message, Dialog dialog);

    /**
     * Returns dialogs where the user is involved
     * 
     * @param eater
     * @return dialogs for the given user
     */
    @Transactional(readOnly = true)
    List<Dialog> getDialogs(Eater eater);


    /**
     * Remove logically a dialog
     * 
     * @param dialog the dialog to delete
     * @param eater the author of the deletion
     */
    @Transactional
    void delete(Dialog dialog, Eater eater);

    /**
     * Return the dialog. All the unread messages in the dialog are set as read.
     * 
     * @param dialogId the id of the dialog 
     * @param eater the user part of the dialog
     * @return the dialog
     */
    @Transactional
    Dialog readDiscussion(String dialogId, Eater eater);
}