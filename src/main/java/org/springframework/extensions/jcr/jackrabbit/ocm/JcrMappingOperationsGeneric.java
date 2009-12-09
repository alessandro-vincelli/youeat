/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.extensions.jcr.jackrabbit.ocm;

import java.util.Collection;

import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.version.VersionIterator;
import org.springframework.dao.DataAccessException;

/**
 * Interface that specifies a basic set of JCR mapping operations. Not often used, but 
 * a useful option to enhance testability, as it can easily be mocked or stubbed.
 * 
 * <p>
 * Provides JcrMappingTemplate's data access methods that mirror various PersistenceManager
 * methods. See the required javadocs for details on those methods.
 * 
 * @author Costin Leau
 *
 */
public interface JcrMappingOperationsGeneric<T> {

    /**
     * Execute a JcrMappingCallback.
     * 
     * @param callback callback to execute
     * @return the callback result
     * @throws DataAccessException
     */
    public T execute(JcrMappingCallback callback) throws DataAccessException;

    /**
     * @see org.apache.jackrabbit.ocm.manager.ObjectContentManager#insert(java.lang.Object)
     */
    public void insert( final T object);

    /**
     * @see org.apache.jackrabbit.ocm.manager.ObjectContentManager#update(java.lang.Object)
     */
    public void update( final T object);

    /**
     * @see org.apache.jackrabbit.ocm.manager.ObjectContentManager#remove(java.lang.String)
     */
    public void remove(final java.lang.String path);

    /**
     * @see org.apache.jackrabbit.ocm.manager.ObjectContentManager#getObject(java.lang.String)
     */
    public T getObject( final java.lang.String path);
    
    /**
     * @see org.apache.jackrabbit.ocm.manager.ObjectContentManager#getObject(org.apache.jackrabbit.ocm.query.Query)
     */
    public T getObject( final Query query);

    /**
     * TODO
     */
    public VersionIterator getAllRevisions( final java.lang.String path);

    /**
     * TODO
     */
    public void checkin(final java.lang.String path);
    
    /**
     * TODO
     */
    public void checkout(final java.lang.String path);
    
    /**
     * TODO
     */
    public String getBaseVersion(final java.lang.String path);
    
    /**
     * TODO
     */
    public T getObjectByVersion(final java.lang.String path, final java.lang.String versionNumber);
    
    /**
     * @see org.apache.jackrabbit.ocm.manager.ObjectContentManager#getObjects(org.apache.jackrabbit.ocm.query.Query)
     */
    public Collection<T> getObjects(final Query query);

}