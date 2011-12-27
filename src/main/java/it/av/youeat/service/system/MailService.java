/**
 * 
 */
package it.av.youeat.service.system;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;

import java.util.Set;

import org.apache.wicket.markup.html.WebPage;

/**
 * Creates and sends email from Youeat
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface MailService {

    /**
     * Send an email to notify the receiver of a new {@link Message}
     * 
     * @param eater the recipient
     * @param message the message to be notified
     * @param page
     * 
     */
    void sendMessageReceivedNotification(Eater eater, Message message, WebPage page);

    /**
     * Send the the given password to the given user by email
     * 
     * @param eater
     * @param newPassword
     */
    void sendPassword(Eater eater, String newPassword);

    /**
     * Send an email to notify the friend suggestion
     *  
     * @param sender the sender of the suggestion
     * @param friendsToSuggest list of friend to suggest
     * @param recipient the recipient of the suggestions and the recipient of the email notification
     * @param page
     * 
     */
    void sendFriendSuggestionNotification(Eater sender, Set<Eater> friendsToSuggest, Eater recipient, WebPage page);
    
    /**
     * Send an email to notify the friend request
     *  
     * @param sender the sender of the friend request
     * @param recipient the recipient of the friend request
     */
    void sendFriendRequestNotification(Eater sender, Eater recipient);
}
