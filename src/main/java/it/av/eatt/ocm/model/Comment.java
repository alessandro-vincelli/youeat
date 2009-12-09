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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
public class Comment extends BasicEntity {

    private String title;
    private String body;
    private String ristorantePath;
    @ManyToOne
    private Eater author;

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

    public String getRistorantePath() {
        return ristorantePath;
    }

    public void setRistorantePath(String ristorantePath) {
        this.ristorantePath = ristorantePath;
    }

}