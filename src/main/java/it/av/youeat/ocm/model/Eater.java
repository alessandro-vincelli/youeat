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

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.data.Country;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.solr.analysis.ISOLatin1AccentFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
@Indexed
@AnalyzerDef(name = "eateranalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = ISOLatin1AccentFilterFactory.class),
        @TokenFilterDef(factory = LowerCaseFilterFactory.class) })
@org.hibernate.annotations.Table(appliesTo = "eater", indexes = { @org.hibernate.annotations.Index(name = "idx_eater_id", columnNames = { "id" }) })
public class Eater extends BasicEntity implements Comparable<Eater> {

    public static final String ID = "id";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    public static final String EMAIL = "email";
    public static final String USERPROFILE = "userProfile";
    public static final String COUNTRY = "country";
    public static final String LANGUAGE = "language";
    public static final String AVATAR = "avatar";
    public static final String SOCIALTYPE = "socialType";
    public static final String SOCIALUID = "socialUID";
    public static final String CREATIONTIME = "creationTime";
    public static final String SEX = "sex";
    
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String passwordSalt;
    @Field(index = Index.NO_NORMS, store = Store.YES)
    @Column(nullable = false)
    private String firstname;
    @Field(index = Index.NO_NORMS, store = Store.YES)
    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String email;
    @ManyToOne(optional=false)
    private Country country;
    private byte[] avatar;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date creationTime;
    private String socialUID;
    private SocialType socialType;
    @Transient
    private String socialSessionKey;
    private Sex sex;
    
    /**
     * used in sign up confirmation
     */
    private boolean confirmed;
    /**
     * used for security reason
     */
    private boolean blocked;

    @ManyToOne(optional=false)
    @ForeignKey(name = "eater_to_langiage_fk")
    private Language language;
    @ManyToOne(optional = false)
    @ForeignKey(name = "eater_to_profile_fk")
    @IndexedEmbedded
    private EaterProfile userProfile;
    @OneToMany(mappedBy = "fromUser", cascade = { CascadeType.ALL })
    @ForeignKey(name = "eater_to_eaterRelation_fk")
    @Fetch(FetchMode.SELECT)
    private List<EaterRelation> userRelation;
    @Version
    private int version;

    /** default constructor */
    public Eater() {
        super();
    }

    /** minimal constructor */
    public Eater(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /** full constructor */
    public Eater(String usPassword, String usFirstname, String usSurname, String usEmail) {
        this.password = usPassword;
        this.firstname = usFirstname;
        this.lastname = usSurname;
        this.email = usEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EaterProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(EaterProfile userProfile) {
        this.userProfile = userProfile;
    }
    
    @XmlTransient
    public List<EaterRelation> getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(List<EaterRelation> userRelation) {
        this.userRelation = userRelation;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @XmlTransient
    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        if (avatar == null) {
            throw new YoueatException("array avatar is null");
        }
        this.avatar = avatar.clone();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getFirstname() + " " + getLastname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Eater o) {
        if (o.getFirstname().equalsIgnoreCase(getFirstname())) {
            return o.getLastname().compareToIgnoreCase(getLastname());
        } else {
            return o.getFirstname().compareToIgnoreCase(getFirstname());
        }
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getSocialUID() {
        return socialUID;
    }

    public void setSocialUID(String socialUID) {
        this.socialUID = socialUID;
    }

    public SocialType getSocialType() {
        return socialType;
    }

    public void setSocialType(SocialType socialType) {
        this.socialType = socialType;
    }

    public String getSocialSessionKey() {
        return socialSessionKey;
    }

    public void setSocialSessionKey(String socialSessionKey) {
        this.socialSessionKey = socialSessionKey;
    }
   
    /**
     * @return true if the user comes from a social network
     */
    public boolean isSocialNetworkEater(){
        return socialType == SocialType.FACEBOOK && socialUID != null;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }
}