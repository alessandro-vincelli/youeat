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

import it.av.eatt.ocm.util.DateUtil;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
public class ActivityRistorante extends Activity {

    public static final String RISTORANTE = "ristorante";
    public static final String TYPE = "type";
    /**
     * Added risto
     */
    public static final String TYPE_ADDED = "added";
    /**
     * Modification on a risto
     */
    public static final String TYPE_MODIFICATION = "modified";
    /**
     * Vote on a risto
     */
    public static final String TYPE_VOTED = "voted";
    /**
     * Added tag
     */
    public static final String TYPE_ADDED_TAG = "added tag";
    /**
     * Removed tag
     */
    public static final String TYPE_REMOVED_TAG = "removed tag";
    /**
     * Added as favourite
     */
    public static final String TYPE_ADDED_AS_FAVOURITE = "added as favourite";
    /**
     * Removed as favourite
     */
    public static final String TYPE_REMOVED_AS_FAVOURITE = "removed as favourite";

    @ManyToOne(optional=false)
    private Ristorante ristorante;
    @Column(nullable=false)
    private String type;
    
    public ActivityRistorante() {
        setDate(DateUtil.getTimestamp());
    }

    public ActivityRistorante(Eater eater, Ristorante ristorante, String type) {
        this();
        setEater(eater);
        this.ristorante = ristorante;
        this.type = type;
    }
    
    public ActivityRistorante(Timestamp date, Eater eater, Ristorante ristorante, String type) {
        setDate(date);
        setEater(eater);
        this.ristorante = ristorante;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Ristorante getRistorante() {
        return ristorante;
    }

    public void setRistorante(Ristorante ristorante) {
        this.ristorante = ristorante;
    }

    public static String getRISTORANTE() {
        return RISTORANTE;
    }

}