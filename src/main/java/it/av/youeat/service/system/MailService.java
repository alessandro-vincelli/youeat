/**
 * 
 */
package it.av.youeat.service.system;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Message;

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
     */
    void sendMessageReceivedNotification(Eater eater, Message message);

    /**
     * Send the the given password to the given user by email
     * 
     * @param eater
     * @param newPassword
     */
    void sendPassword(Eater eater, String newPassword);
}
