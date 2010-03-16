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

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

/**
 * Common operations
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public interface ApplicationService<T> {
    /**
     * Save a T item
     * 
     * @param item
     * @return just saved T item
     */
    T save(T item);

    /**
     * Return all the T item
     * 
     * @return all T item
     */
    List<T> getAll();

    /**
     * Search on the T item by the given text value
     * 
     * @param pattern
     * @return found T item
     */
    @Deprecated
    List<T> findFullText(String pattern);

    /**
     * Remove the given T item
     * 
     * @param item
     */
    void remove(T item);

    /**
     * Get an item by ID
     * 
     * @param id
     * @return T item
     */
    T getByID(String id);
    
    /**
     * find using hibernate criteria
     * 
     * @param criterion 
     * @return list of found objects
     */
    List<T> findByCriteria(Criterion... criterion);
    
    /**
     * find using hibernate criteria
     * 
     * @param order
     * @param firstResult
     * @param maxResults
     * @param criterion
     * @return list of found objects
     */
    List<T> findByCriteria(Order order, int firstResult, int maxResults, Criterion[] criterion);
}