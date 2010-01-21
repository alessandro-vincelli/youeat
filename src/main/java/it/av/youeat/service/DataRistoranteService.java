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

import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.model.ProvIta;
import it.av.youeat.ocm.model.data.Country;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services on {@link DataRistorante}
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Service
@Repository
public interface DataRistoranteService {

    /**
     * Update a restaurant
     * 
     * @param risto the Risto to be modified
     * @return DataRistorante
     */
    @Transactional
    DataRistorante update(DataRistorante risto);

    /**
     * Insert a new restaurant
     * 
     * @param risto the Risto to be inserted
     * @return DataRistorante
     */
    @Transactional
    DataRistorante insert(DataRistorante risto);

    /**
     * Return all the restaurant
     * 
     * @return Collection<DataRistorante>
     */
    @Transactional(readOnly = true)
    Collection<DataRistorante> getAll();

    /**
     * Find the restaurant by the given pattern
     * 
     * @param pattern
     * @param maxResults the max number of results, 0 for unlimited
     * @return Collection<DataRistorante>
     */
    @Transactional(readOnly = true)
    Collection<DataRistorante> find(String pattern, int maxResults);

    /**
     * Find the restaurant by the given pattern, city, country
     * 
     * @param pattern
     * @param city
     * @param country
     * @param maxResults the max number of results, 0 for unlimited
     * @return Collection<DataRistorante>
     */
    @Transactional(readOnly = true)
    Collection<DataRistorante> find(String pattern, String city, Country country, int maxResults);

    /**
     * Remove a restaurant from the repository
     * 
     * @param risto
     */
    @Transactional
    void remove(DataRistorante risto);

    /**
     * Get a restaurant by ID
     * 
     * @param id
     * @return DataRistorante
     */
    @Transactional(readOnly = true)
    DataRistorante getByID(String id);

    @Transactional(readOnly = true)
    public List<ProvIta> getAllProv();

    /**
     * exact match search on given parameters
     * 
     * @param pattern
     * @param city
     * @param country
     * @return found datRisto
     */
    @Transactional(readOnly = true)
    Collection<DataRistorante> getBy(String pattern, String city, Country country);

    /**
     * create/update lucene index on data_ristorante 
     */
    @Transactional    
    void indexData();

    /**
     * Find restaurants by the given pattern, using Lucene
     * 
     * @param pattern
     * @param maxResult max number of results, 0 or negative for unlimited results
     * @return found risto
     */
    @Transactional(readOnly=true)
    List<DataRistorante> freeTextSearch(String pattern, int maxResult);
    
    /**
     * Find restaurants by the given pattern, using Lucene
     * 
     * @param pattern
     * @param city (optional)
     * @param country, 2 characters ISO (optional)
     * @param maxResult max number of results, 0 or negative for unlimited results
     * @return found risto
     */
    @Transactional(readOnly=true)
    List<DataRistorante> freeTextSearch(String pattern, String city, String country, int maxResult);

}