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

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.Ristorante;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Operations on the repo for Ristoranti
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Service
@Repository
@Transactional
public interface JcrRistoranteService {

    /**
     * Store the given restaurant in repo and create a new version
     * 
     * @param risto the Risto to be committed
     * @return Ristorante
     * @throws JackWicketException
     */
	@Transactional
    void commit(Ristorante risto) throws JackWicketException;
        
    /**
     * Get all the revision of the restaurant on this path 
     * 
     * @param path
     * @return List<Ristorante>
     * @throws JackWicketException
     */
    @Transactional(readOnly = true)
    List<Ristorante> getAllRevisions(String path) throws JackWicketException;
 
    /**
     * Get all the revisions of restaurant by ID 
     * 
     * @param id
     * @return List<Ristorante>
     * @throws JackWicketException
     */
    @Transactional(readOnly = true)
    List<Ristorante> getAllRevisionsById(long id) throws JackWicketException;
 
    /**
     * Remove the given restaurant from the repo
     * 
     * @param risto the Risto to be removed
     * @throws JackWicketException
     */
    @Transactional
    void remove(Ristorante risto) throws JackWicketException;

    
}
