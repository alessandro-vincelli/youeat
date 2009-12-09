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
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.jackrabbit.ocm.manager.beanconverter.impl.ReferenceBeanConverterImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
@Table(
        //uniqueConstraints = {@UniqueConstraint(columnNames={"from_user_id", "to_user_id"})}
)
public class EaterRelation extends BasicEntity{

    public final static String STATUS_ACTIVE = "active";
    public final static String STATUS_PENDING = "pending";
    public final static String STATUS_IGNORED = "ignored";
    
    public final static String TYPE_FRIEND = "friend";
    public final static String TYPE_FOLLOW = "follow";

    public final static String START_DATE = "startDate";
    public final static String ENDD_ATE = "endDate";
    public final static String STATUS = "status";
    public final static String TYPE = "type";
    public final static String FROM_USER = "fromUser";
    public final static String TO_USER = "toUser";
    @Field
    private Timestamp startDate;
    @Field
    private Timestamp endDate;
    @Field
    private String status;
    @Field
    private String type;
    @Bean(converter = ReferenceBeanConverterImpl.class, jcrMandatory=true, jcrType = "nt:unstructured")
    @ManyToOne
    private Eater fromUser;
    @Bean(converter = ReferenceBeanConverterImpl.class, jcrMandatory=true, jcrType = "nt:unstructured")
    @OneToOne
    private Eater toUser;

    
    public static EaterRelation createFollowRelation(Eater fromUser, Eater toUser){
        EaterRelation relation = new EaterRelation();
        relation.setStartDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        relation.setStatus(STATUS_ACTIVE);
        relation.setType(TYPE_FOLLOW);
        relation.setFromUser(fromUser);
        relation.setToUser(toUser);
        return relation;
    }
    
    public static EaterRelation createFriendRelation(Eater fromUser, Eater toUser){
        EaterRelation relation = new EaterRelation();
        relation.setStartDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        relation.setStatus(STATUS_PENDING);
        relation.setType(TYPE_FRIEND);
        relation.setFromUser(fromUser);
        relation.setToUser(toUser);
        return relation;
    }
    
    public EaterRelation() {}
 
    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Eater getFromUser() {
        return fromUser;
    }

    public void setFromUser(Eater fromUser) {
        this.fromUser = fromUser;
    }

    public Eater getToUser() {
        return toUser;
    }

    public void setToUser(Eater toUser) {
        this.toUser = toUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}