/**
 * 
 */
package it.av.youeat.ocm.model;

import java.util.Comparator;

/**
 * Comparator on {@link Message} based on sent time
 * 
 */
public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message o1, Message o2) {
        return o1.getSentTime().compareTo(o2.getSentTime());
    }

}
