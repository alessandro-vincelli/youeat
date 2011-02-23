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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.solr.analysis.ISOLatin1AccentFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 * Static list of restaurants imported from severals parts, are used only to suggest info to the users
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
@Indexed
@AnalyzerDef(name = "dataristoranteanalyzer", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = ISOLatin1AccentFilterFactory.class),
        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
        @TokenFilterDef(factory = StopFilterFactory.class, params = {
                @Parameter(name = "words", value = "properties/stoplist.properties"),
                @Parameter(name = "ignoreCase", value = "true") }) })
public final class DataRistorante extends BasicEntity {

    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String POSTALCODE = "postalCode";
    public static final String COUNTRY = "country";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String WWW = "www";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String MOBILE_PHONE_NUMBER = "mobilePhoneNumber";
    public static final String FAX_NUMBER = "faxNumber";
    public static final String IMPORTED = "imported";

    @Field(index = Index.TOKENIZED, store = Store.YES)
    @Analyzer(definition = "dataristoranteanalyzer")
    private String name;
    private String address;
    private String postalCode;
    @Field(index=Index.TOKENIZED, store=Store.YES)
    private String country;
    private String province;
    @Field(index=Index.TOKENIZED, store=Store.YES)
    private String city;
    private String type;
    private String www;
    @Field(index = Index.TOKENIZED, store = Store.COMPRESS)
    @Analyzer(definition = "dataristoranteanalyzer")
    private String description;
    private Timestamp creationTime;
    private String phoneNumber;
    private String mobilePhoneNumber;
    private String faxNumber;
    private boolean imported;
	
    
    
    public DataRistorante() {};
    
	public DataRistorante(String name, String address, String postalCode, String country, String province, String city, String type, String www, String description,
            Timestamp creationTime, String phoneNumber, String mobilePhoneNumber, String faxNumber) {
	    super();
	    this.name = name;
	    this.address = address;
	    this.postalCode = postalCode;
	    this.country = country;
	    this.province = province;
	    this.city = city;
	    this.type = type;
	    this.www = www;
	    this.description = description;
	    this.creationTime = creationTime;
	    this.phoneNumber = phoneNumber;
	    this.mobilePhoneNumber = mobilePhoneNumber;
	    this.faxNumber = faxNumber;
    }
	/**
     * @return the name
     */
    public String getName() {
    	return name;
    }
	/**
     * @return the address
     */
    public String getAddress() {
    	return address;
    }
	/**
     * @return the postalCode
     */
    public String getPostalCode() {
    	return postalCode;
    }
	/**
     * @return the country
     */
    public String getCountry() {
    	return country;
    }
	/**
     * @return the province
     */
    public String getProvince() {
    	return province;
    }
	/**
     * @return the city
     */
    public String getCity() {
    	return city;
    }
	/**
     * @return the type
     */
    public String getType() {
    	return type;
    }
	/**
     * @return the www
     */
    public String getWww() {
    	return www;
    }
	/**
     * @return the description
     */
    public String getDescription() {
    	return description;
    }
	/**
     * @return the creationTime
     */
    public Timestamp getCreationTime() {
    	return creationTime;
    }
	/**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
    	return phoneNumber;
    }
	/**
     * @return the mobilePhoneNumber
     */
    public String getMobilePhoneNumber() {
    	return mobilePhoneNumber;
    }
	/**
     * @return the faxNumber
     */
    public String getFaxNumber() {
    	return faxNumber;
    }
	
    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
  
}