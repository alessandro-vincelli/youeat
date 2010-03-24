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
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Entity
@javax.persistence.Table(name = "ristorante_revision")
@Table(appliesTo = "ristorante_revision", indexes = {
        @Index(name = "ristoRevision_revisionnumber_index", columnNames = { "revisionNumber" }),
        @Index(name = "ristoRevision_modificationtime_index", columnNames = { "modificationTime" }) })
public class RistoranteRevision extends BasicEntity{

    public static final String RISTORANTEREVISION = "ristoranteRevision";
    public static final String ID_RISTORANTE = "ristorante_id";

    private int revisionNumber;
    
    private String name;
    @Field
    private String address;

    private String postalCode;

    @ManyToOne
    private Country country;

    private String province;

    @IndexedEmbedded
    @ManyToOne
    private City city;

    private String type;

    private String www;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;

    private String phoneNumber;

    private String mobilePhoneNumber;

    @IndexedEmbedded
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @Fetch(FetchMode.SELECT)
    @JoinTable
    private List<RistoranteDescriptionI18n> descriptions;
    /**
     * the field is used only to show difference on description
     */
    @Transient
    private transient String descriptionDiff;
    
    private double longitude;
    private double latitude;

    public RistoranteRevision() {
    }

    public RistoranteRevision(Ristorante risto) throws YoueatException {
        super();
        try {
            BeanUtils.copyProperties(risto, this);
            this.setId(null);
            List<RistoranteDescriptionI18n> copiedDescriptionI18ns = new ArrayList<RistoranteDescriptionI18n>(risto
                    .getDescriptions().size());
            for (RistoranteDescriptionI18n descriptionI18n : risto.getDescriptions()) {
                RistoranteDescriptionI18n descriptionI18nNew = new RistoranteDescriptionI18n();
                BeanUtils.copyProperties(descriptionI18n, descriptionI18nNew);
                descriptionI18nNew.setId(null);
                copiedDescriptionI18ns.add(descriptionI18nNew);
            }
            this.setDescriptions(copiedDescriptionI18ns);
        } catch (BeansException e) {
            throw new YoueatException(e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public List<RistoranteDescriptionI18n> getDescriptions() {
        return descriptions;
    }

    public final void setDescriptions(List<RistoranteDescriptionI18n> descriptions) {
        this.descriptions = descriptions;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public static String getRistoranterevision() {
        return RISTORANTEREVISION;
    }

    public static String getIdRistorante() {
        return ID_RISTORANTE;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    /**
     * @return the descriptionDiff
     */
    public String getDescriptionDiff() {
        return descriptionDiff;
    }

    /**
     * @param descriptionDiff the descriptionDiff to set
     */
    public void setDescriptionDiff(String descriptionDiff) {
        this.descriptionDiff = descriptionDiff;
    }

    /**
     * return the description for the current language, empty description otherwise
     * 
     * @param language the language to check
     * @return a description
     */
    public RistoranteDescriptionI18n getDesctiptionByLanguage(Language language) {
        RistoranteDescriptionI18n i18n = new RistoranteDescriptionI18n(language);
        for (RistoranteDescriptionI18n ristoranteDescriptionI18n : this.getDescriptions()) {
            if (ristoranteDescriptionI18n.getLanguage().equals(language)) {
                i18n = ristoranteDescriptionI18n;
            }
        }
        return i18n;
    }
    
//    @Override
//    public RistoranteRevision clone() {
//        return BeanUtils.;
//    }

}