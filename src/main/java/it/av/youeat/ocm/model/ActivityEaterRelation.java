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

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Entity
public class ActivityEaterRelation extends Activity {

    /**
     * an eater starts following another
     */
    public static final String TYPE_STARTS_FOLLOWING = "starts following";
    /**
     * two eater are now friends
     */
    public static final String TYPE_ARE_FRIENDS = "are friends";

    public static final String EATER_ACTIVITY_TYPE = "eaterActivityType";
    public static final String WITH_USER = "withUser";

    private String eaterActivityType;
    @ManyToOne(optional=false)
    @ForeignKey(name="activity_relation_with_user_fk")
    private Eater withUser;

    public ActivityEaterRelation() {
        super();
    }

    public ActivityEaterRelation(Timestamp date, Eater user) {
        super(date, user);
    }

    public ActivityEaterRelation(Timestamp date, Eater eater, Eater withUser, String eaterActivityType) {
        super.setDate(date);
        super.setEater(eater);
        this.withUser = withUser;
        this.eaterActivityType = eaterActivityType;
    }

    public String getEaterActivityType() {
        return eaterActivityType;
    }

    public void setEaterActivityType(String eaterActivityType) {
        this.eaterActivityType = eaterActivityType;
    }

    public Eater getWithUser() {
        return withUser;
    }

    public void setWithUser(Eater withUser) {
        this.withUser = withUser;
    }

}