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
package it.av.youeat.ocm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

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
    

    public static final int BODY_MAX_LENGTH = 10000;
    public static final int TITLE_MAX_LENGTH = 160;
    
    @ManyToOne
    @JoinColumn(name="dialog_id", insertable=false, updatable=false, nullable=false)
    @Index(name="idx_massage_to_dialog")
    private Dialog dialog;
    @ManyToOne
    @ForeignKey(name = "message_to_sender_author_fk")
    @Index(name="idx_massage_sender")
    private Eater sender;
    @Column(length = TITLE_MAX_LENGTH, updatable=false)
    private String title;
    @Column(length = BODY_MAX_LENGTH, updatable=false)
    private String body;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable=false)
    @Index(name="idx_message_readtime")
    private Date sentTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date readTime;
    
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

    public Eater getSender() {
        return sender;
    }

    public void setSender(Eater sender) {
        this.sender = sender;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

}