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

import it.av.eatt.YoueatException;

import javax.persistence.Entity;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
public class RistorantePicture extends BasicEntity {

    private byte[] picture;
    private boolean active;
    private String description;
    private String title;

    public RistorantePicture() {
        super();
    }

    /**
     * @param picture (not null)
     * @param active
     */
    public RistorantePicture(byte[] picture, boolean active) {
        super();
        if (picture == null) {
            throw new YoueatException("array picture is null");
        }
        this.picture = picture.clone();
        this.active = active;
    }

    /**
     * @return the picture
     */
    public byte[] getPicture() {
        return picture;
    }

    /**
     * @param picture the picture to set (not null)
     */
    public void setPicture(byte[] picture) {
        if (picture == null) {
            throw new YoueatException("array picture is null");
        }
        this.picture = picture.clone();
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

}