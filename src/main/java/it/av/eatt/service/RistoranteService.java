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
package it.av.eatt.service;

import it.av.eatt.ocm.model.Eater;
import it.av.eatt.ocm.model.Ristorante;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services on Ristoranti
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 */
@Service
@Repository
public interface RistoranteService {
    /**
     * Update a restaurant
     * 
     * @param risto the Risto to be modified
     * @param user the user that is performing the modification
     * @return ristorante
     */
    @Transactional
    Ristorante update(Ristorante risto, Eater user);

    /**
     * Insert a new restaurant
     * 
     * @param risto the Risto to be inserted
     * @param user the user that is performing the modification
     * @return Ristorante
     */
    @Transactional
    Ristorante insert(Ristorante risto, Eater user);

    /**
     * Return all the restaurant
     * 
     * @return Collection<Ristorante>
     */
    @Transactional(readOnly = true)
    Collection<Ristorante> getAll();

    /**
     * Find the restaurant by the given pattern
     * 
     * @param pattern
     * @return Collection<Ristorante>
     */
    @Transactional(readOnly = true)
    Collection<Ristorante> find(String pattern);

    /**
     * Find restaurants by the given pattern, using Lucene
     * 
     * @param pattern
     * @return List<Ristorante>
     */
    @Transactional(readOnly = true)
    List<Ristorante> freeTextSearch(String pattern);

    /**
     * Remove a restaurant from the repository
     * 
     * @param risto
     */
    @Transactional
    void remove(Ristorante risto);

    /**
     * Get a restaurant by ID
     * 
     * @param id the id of the restaurant
     * @return Ristorante ristorante
     */
    @Transactional(readOnly = true)
    Ristorante getByID(String id);

    /**
     * Add a rate on the given restaurant by the given user
     * 
     * @param risto
     * @param user
     * @param rate
     * @return just rated risto
     */
    @Transactional
    Ristorante addRate(Ristorante risto, Eater user, int rate);

    /**
     * check if the given user has already voted the given risto
     * 
     * @param risto
     * @param user
     * @return boolean
     */
    @Transactional(readOnly = true)
    boolean hasUsersAlreadyRated(Ristorante risto, Eater user);

    /**
     * Update a restaurant without create a new revision
     * 
     * @param ristorante
     * @return just saved ristorante
     */
    @Transactional
    Ristorante updateNoRevision(Ristorante ristorante);
}
