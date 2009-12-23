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
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
public class Comment extends BasicEntity {

    public static final String TITLE_FIELD = "title";
    public static final String BODY_FIELD = "body";
    public static final String CREATIONTIME_FIELD = "creationTime";
    public static final String AUTHOR_FIELD = "author";

    public static final int BODY_MAX_LENGTH = 1000;
    public static final int TITLE_MAX_LENGTH = 160;

    @Column(length = TITLE_MAX_LENGTH)
    private String title;
    @Column(length = BODY_MAX_LENGTH)
    private String body;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date creationTime;
    /**
     * to moderate the comment
     */
    private boolean enabled;

    @ManyToOne
    @ForeignKey(name = "comment_to_author_fk")
    private Eater author;

    /**
     * the default constructor sets the the comment as enabled
     */
    public Comment() {
        super();
        this.enabled = true;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getBody() {
        return body;
    }

    public final void setBody(String body) {
        this.body = body;
    }

    public final Eater getAuthor() {
        return author;
    }

    public final void setAuthor(Eater author) {
        this.author = author;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}