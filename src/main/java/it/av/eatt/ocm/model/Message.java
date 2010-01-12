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
package it.av.eatt.ocm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

/**
 * Message between {@link Eater}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
public class Message extends BasicEntity {

    public static final String TITLE_FIELD = "title";
    public static final String BODY_FIELD = "body";
    public static final String SENTTIME_FIELD = "sentTime";
    public static final String READTIME_FIELD = "readTime";
    public static final String SENDER_FIELD = "sender";
    public static final String RECEIVER_FIELD = "receiver";
    public static final String DELETED_FIELD = "deleted";
    public static final String ISRECEIVED_FIELD = "isReceived";
    public static final String ISPRIVATE_FIELD = "isPrivate";


    public static final int BODY_MAX_LENGTH = 10000;
    public static final int TITLE_MAX_LENGTH = 160;

    @Column(length = TITLE_MAX_LENGTH)
    private String title;
    @Column(length = BODY_MAX_LENGTH)
    private String body;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date sentTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date readTime;
    /**
     * to logic delete
     */
    private boolean deleted;
    private boolean isPrivate;
    /**
     * true if  received, false if sent 
     */
    private boolean isReceived;

    @ManyToOne
    @ForeignKey(name = "message_to_sender_author_fk")
    private Eater sender;

    @ManyToOne
    @ForeignKey(name = "message_to_receiver_author_fk")
    private Eater receiver;

    /**
     * the default constructor
     */
    public Message() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }
}