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

import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.BasicNode;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * General operation on the JCR repository
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
@Service
@Transactional
public interface JcrApplicationService<T extends BasicNode> {
    /**
     * Update an already present T item
     * 
     * @param item
     * @return just updated item
     * @throws YoueatException
     */
    @Transactional
    T update(T item) throws YoueatException;
    
    /**
     * Insert a new item of type T in the repo
     * 
     * @param item
     * @return just inserted item
     * @throws YoueatException
     */
    @Transactional
    T insert(T item) throws YoueatException;

    /**
     * Return oll item of the type T
     * 
     * @return all the T components
     * @throws YoueatException
     */
    @Transactional(readOnly = true)
    Collection<T> getAll() throws YoueatException;

    /**
     * Search on the T item by the given text value
     * 
     * @param pattern
     * @return found T item
     * @throws YoueatException
     */
    @Transactional(readOnly = true)
    Collection<T> find(String pattern) throws YoueatException;

    /**
     * Remove the T item
     * 
     * @param item
     * @throws YoueatException
     */
    @Transactional
    void remove(T item) throws YoueatException;

    /**
     * If the T item is versionable return all the revisions
     * 
     * @param path
     * @return all the revisions
     * @throws YoueatException
     */
    @Transactional(readOnly = true)
    List<T> getAllRevisions(String path) throws YoueatException;

    /**
     * Return the T item by the path
     * 
     * @param path
     * @return 
     * @throws YoueatException
     */
    @Transactional(readOnly = true)
    T getByPath(String path) throws YoueatException;

    /**
     * If the T item has an uuid return the T object with the given uuid
     * @param uuid
     * @return the object with the given uuid
     * @throws YoueatException
     */
    @Transactional(readOnly = true)
    T getByUuid(String uuid) throws YoueatException;

}
