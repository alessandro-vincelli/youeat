/**
 * 
 */
package it.av.youeat.ocm.model;

import java.util.Date;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Where;

/**
 * Represents a dialog between two user
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
public class Dialog extends BasicEntity {
    
    public static final String CREATION_TIME_FIELD = "creationTime";
    public static final String SENDER_FIELD = "sender";
    public static final String RECEIVER_FIELD = "receiver";
    public static final String DELETED_FROM_RECEIVER_FIELD = "deletedFromReceiver";
    public static final String DELETED_FROM_SENDER_FIELD = "deletedFromSender";
    public static final String ISPRIVATE_FIELD = "isPrivate";

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @Sort(type = SortType.COMPARATOR, comparator = MessageComparator.class)
    @Where(clause="1=1")
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name="dialog_id", nullable=false)
    private SortedSet<Message> messages;
    @ManyToOne
    @ForeignKey(name = "message_to_sender_author_fk")
    private Eater sender;

    @ManyToOne
    @ForeignKey(name = "message_to_receiver_author_fk")
    private Eater receiver;
    private boolean isPrivate;
    /**
     * to logic delete for sender
     */
    private boolean deletedFromSender;
    /**
     * to logic delete for receiver
     */
    private boolean deletedFromReceiver;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationTime;

    /**
     * Constructor 
     */
    public Dialog() {
        super();
        this.messages = new ConcurrentSkipListSet<Message>(new MessageComparator());
    }

    public SortedSet<Message> getMessages() {
        return messages;
    }

    public void setMessages(SortedSet<Message> messages) {
        this.messages = messages;
    }

    public Eater getSender() {
        return sender;
    }

    public void setSender(Eater sender) {
        this.sender = sender;
    }

    public Eater getReceiver() {
        return receiver;
    }

    public void setReceiver(Eater receiver) {
        this.receiver = receiver;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isDeletedFromSender() {
        return deletedFromSender;
    }

    public void setDeletedFromSender(boolean deletedFromSender) {
        this.deletedFromSender = deletedFromSender;
    }

    public boolean isDeletedFromReceiver() {
        return deletedFromReceiver;
    }

    public void setDeletedFromReceiver(boolean deletedFromReceiver) {
        this.deletedFromReceiver = deletedFromReceiver;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
    
}