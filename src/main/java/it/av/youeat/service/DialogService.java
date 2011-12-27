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
import java.util.Set;

import org.apache.wicket.markup.html.WebPage;

/**
 * Offer operation to create a dialog between two users
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface DialogService {

    /**
     * Create a new dialog containing the new message.
     * 
     * @param sender the creator of the dialog
     * @param recipient the receiver of the first message of the dialog
     * @param message to send
     * @param page
     * 
     * @return the created dialog
     */
    Dialog startNewDialog(Eater sender, Eater recipient, Message message, WebPage page);

    /**
     * Reply wit a message in the dialog.
     * 
     * @param message message to send
     * @param dialog the dialog for the message
     * @param recipient the recipient of the reply
     * @param page
     * 
     * @return the sent message
     */
    Dialog reply(Message message, Dialog dialog, Eater recipient, WebPage page);

    /**
     * Returns dialogs created(sent) by the given user not flagged as removed
     * 
     * @param eater
     * @return dialogs for the given user
     */
    List<Dialog> getCreatedDialogs(Eater eater);

    /**
     * Count number of created(sent) dialogs by the given user not flagged as removed
     * 
     * @param eater
     * @return dialogs for the given user
     */
    int countCreatedDialogs(Eater eater);

    /**
     * Returns dialogs where the user is involved. Not flagged as removed
     * 
     * @param eater
     * @param excludeSingleMessage true to exclude dialogs with only 1 message
     * @return dialogs for the given user
     */
    List<Dialog> getDialogs(Eater eater, boolean excludeSingleMessage);

    /**
     * Remove logically a dialog
     * 
     * @param dialog the dialog to delete
     * @param eater the author of the deletion
     */
    void delete(Dialog dialog, Eater eater);

    /**
     * Return the dialog. All the unread messages in the dialog are set as read.
     * 
     * @param dialogId the id of the dialog
     * @param eater the user part of the dialog
     * @return the dialog
     */
    Dialog readDiscussion(String dialogId, Eater eater);

    /**
     * Remove all the dialogs where the eater is involved
     * 
     * @param eater
     */
    void removeByEater(Eater eater);

    /**
     * send messages to a list of friends to suggest a new friend
     * 
     * @param sender the sender of the suggestion
     * @param recipient the receiver of the suggestions
     * @param friendsToSuggest the list of friends to suggest
     * @param page
     */
    void sendFriendSuggestions(Eater sender, Eater recipient, Set<Eater> friendsToSuggest, WebPage page);
}