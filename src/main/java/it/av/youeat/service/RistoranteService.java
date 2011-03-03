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

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Services on Ristoranti
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
public interface RistoranteService {
    /**
     * Update a ristorante
     * 
     * @param risto the Risto to be modified
     * @param user the user that is performing the modification
     * @return ristorante
     */
    Ristorante update(Ristorante risto, Eater user);

    /**
     * Update a ristorante, and refresh latitude and longitude
     * 
     * @param risto the Risto to be modified
     * @param user the user that is performing the modification
     * @return ristorante
     */
    Ristorante updateAddress(Ristorante risto, Eater user);
    
    /**
     * Save the risto and update the location.
     * The update doesn't create any activity or revisions.
     * 
     * @param risto
     * @return ristorante 
     */
    Ristorante updateLatitudeLongitude(Ristorante risto);

    /**
     * Insert a new ristorante
     * 
     * @param risto the Risto to be inserted
     * @param user the user that is performing the modification
     * @return ristorante
     */
    Ristorante insert(Ristorante risto, Eater user);

    /**
     * Return all the ristoranti
     * 
     * @return Collection<Ristorante>
     */
    Collection<Ristorante> getAll();
    
    /**
     * Return all the ristoranti, with set only id, Name, City, modification Time.<br>
     * <b>Use only for readonly</b><br>
     * <b>the returned object are not persistent object</b>
     * 
     * @return Collection<Ristorante>
     */
    Collection<Ristorante> getAllSimple();

    /**
     * Find the ristorante by the given pattern
     * 
     * @param pattern
     * @return Collection<Ristorante>
     */
    Collection<Ristorante> find(String pattern);

    /**
     * Find restaurants using the given pattern.
     * <p>
     * <b>Note: fuzzy free text search performed on the <b>name</b>, <b>city</b>, and <b>tags</b> of the risto
     * 
     * @param pattern
     * @return found ristos
     */
    List<Ristorante> freeTextSearch(String pattern);
        
    /**
     * Find restaurants using the given pattern.
     * <p>
     * <b>Note: fuzzy free text search performed on the <b>name</b>, <b>city</b>, and <b>tags</b> of the risto
     * 
     * @param pattern
     * @param firstResult position of the first result, numbered from 0 (-1 for default)
     * @param maxResult max number of result to retrieve (-1 for default)
     * @return found ristos
     */
    List<Ristorante> freeTextSearch(String pattern, int firstResult, int maxResult);

    /**
     * Find restaurants using the given pattern and city.
     * <p>
     * <b>Note: fuzzy free text search performed only on the <b>name</b> of the risto.
     * 
     * @param pattern
     * @param city (NULL to disable)
     * @return found ristos
     */
    List<Ristorante> freeTextSearchOnName(String pattern, City city);

    /**
     * Remove a ristorante
     * 
     * @param risto
     */
    void remove(Ristorante risto);

    /**
     * Get a ristorante by ID
     * 
     * @param id the id of the ristorante
     * @return Ristorante ristorante
     */
    Ristorante getByID(String id);

    /**
     * Add a rate on the given ristoranti by the given user
     * 
     * @param risto
     * @param user
     * @param rate
     * @return just rated risto
     */
    Ristorante addRate(Ristorante risto, Eater user, int rate);

    /**
     * check if the given user has already voted the given ristorante
     * 
     * @param risto
     * @param user
     * @return boolean
     */
    boolean hasUsersAlreadyRated(Ristorante risto, Eater user);

    /**
     * Update a ristorante without create a new revision
     * 
     * @param ristorante
     * @return just saved ristorante
     */
    Ristorante updateNoRevision(Ristorante ristorante);

    /**
     * Return the last added ristoranti
     * 
     * @param numberOfResult number of result to return
     * @return lasts risto added
     */
    List<Ristorante> getLastsAdded(int numberOfResult);

    /**
     * Return the last modified ristoranti
     * 
     * @param numberOfResult number of result to return
     * @return lasts risto modified
     */
    List<Ristorante> getLastsModified(int numberOfResult);

    /**
     * Return ristoranti in the given city
     * 
     * @param city
     * @param firstResult first result
     * @param maxResults number of results
     * @return found ristos
     */
    List<Ristorante> getByCity(City city, int firsResult, int maxResults);

    /**
     * Return cities with risto
     * 
     * @return found ristos
     */
    List<City> getCityWithRisto();

    /**
     * Return cities with risto by the given country
     * 
     * @return found cities
     */
    List<City> getCityWithRistoByCountry(Country country);

    /**
     * Return countries with risto
     * 
     * @return found countries
     */
    List<Country> getCountryWithRisto();

    /**
     * Return random list of ristoranti
     * 
     * @param numberOfResult number of result to return
     * @return lasts risto modified
     */
    List<Ristorante> getRandom(int numberOfResult);

    /**
     * Find restaurants by the given pattern, using Lucene and filtering activties for the given map of users
     * 
     * @param pattern
     * @param eaters
     * @return List<Ristorante>
     */
    List<Ristorante> freeTextSearch(String pattern, ArrayList<Eater> eaters);
    
    /**
     * Find restaurants by the given pattern, using Lucene and filtering activties for the given map of users
     * 
     * @param pattern
     * @param eaters
     * @param firstResult position of the first result, numbered from 0 (-1 for default)
     * @param maxResult max number of result to retrieve (-1 for default)
     * @return List<Ristorante>
     */
    List<Ristorante> freeTextSearch(String pattern, ArrayList<Eater> eaters, int firstResult, int maxResult);

    /**
     * count risto in the DB
     * 
     * @return number of risto
     */
    int count();

    /**
     * create/update lucene index on ristoranti
     */
    void indexData();
    
    /**
     * Count risto on the given search patter
     * 
     * @param pattern
     * @param eaters filter on user (null to disable)
     * @return
     */
    int countfreeTextSearch(String pattern, ArrayList<Eater> eaters);

}
