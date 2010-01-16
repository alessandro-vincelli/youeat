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

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Restaurants imported from severals part, are used only to suggest info to the users
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Entity
public class ProvIta {

    public static final String PROV = "prov";
	@Id
    private  String prov;

	/**
     * @return the prov
     */
    public String getProv() {
    	return prov;
    }

	/**
     * @param prov the prov to set
     */
    public void setProv(String prov) {
    	this.prov = prov;
    }
	
}