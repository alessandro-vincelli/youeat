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

import java.sql.Timestamp;

import javax.persistence.Entity;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
public class ActivityUserRelation extends Activity{
    
    public static final String USER_ACTIVITY_TYPE = "userActivityType";
    public static final String WITH_USER = "withUser";

    private String userActivityType;
    private Eater withUser;

    public ActivityUserRelation() {
        super();
    }

    public ActivityUserRelation(Timestamp date, Eater user) {
        super(date, user);
        // TODO Auto-generated constructor stub
    }

    public ActivityUserRelation(Timestamp date, Eater eater, Eater withUser, String userActivityType) {
        super.setDate(date);
        super.setEater(eater);
        this.withUser = withUser;
        this.userActivityType = userActivityType;
    }

    public String getUserActivityType() {
        return userActivityType;
    }

    public void setUserActivityType(String userActivityType) {
        this.userActivityType = userActivityType;
    }

    public Eater getWithUser() {
        return withUser;
    }

    public void setWithUser(Eater withUser) {
        this.withUser = withUser;
    }


}