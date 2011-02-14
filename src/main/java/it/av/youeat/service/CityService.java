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
package it.av.youeat.service;

import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;

import java.util.List;

/**
 * Services on {@Link City}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface CityService extends ApplicationService<City> {

    /**
     * Find the restaurant using the given pattern
     * 
     * @param string
     * @param maxResults
     * @return found cities
     */
    List<City> find(String string, int maxResults);
    
    /**
     * Find the restaurant using the given pattern
     * 
     * @param string
     * @param country
     * @param maxResults
     * @return found cities
     */
    List<City> find(String string, Country country, int maxResults);

    /**
     * Find a city using an exact match (case insensitive) on the given cityName and country
     * 
     * @param cityName
     * @param country
     * @return a city
     */
    City getByNameAndCountry(String cityName, Country country);

    /**
     * Find the city using the given pattern, and return only the cityName
     * 
     * @param name name of the ristorant
     * @param country
     * @param maxResults
     * @return names of cities
     */
    List<String> findByName(String name, Country country, int maxResults);

    /**
     * Find the city using the given pattern, and return only the cityName
     * 
     * @param string
     * @param maxResults
     * @return found cities
     */
    List<String> findName(String string, int maxResults);

    /**
     * Renews the lucene index on the cities 
     */
    void indexData();
}