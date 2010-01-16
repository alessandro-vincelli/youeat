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
package it.av.youeat.service.impl;

import it.av.youeat.UserAlreadyExistsException;
import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.BasicNode;
import it.av.youeat.service.JcrApplicationService;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.apache.jackrabbit.ocm.version.Version;
import org.apache.jackrabbit.ocm.version.VersionIterator;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.extensions.jcr.jackrabbit.ocm.JcrMappingTemplateGeneric;
import org.springframework.transaction.annotation.Transactional;
/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a> 
 * 
 * @param <T>
 */
@Transactional
public class JcrApplicationServiceJackrabbit<T extends BasicNode> implements JcrApplicationService<T>{

    @SpringBean
    private JcrMappingTemplateGeneric<T> jcrMappingtemplate;
    /**
     * /Base Path doesn't contains final slash 
     */
    private String basePath;

    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#update(it.av.youeat.ocm.model.BasicNode)
     */
    @Transactional
    public T update(T object) throws YoueatException {
        if(object == null || object.getPath() == null){
            throw new YoueatException("Object or/and object's path is null");
        }
        jcrMappingtemplate.checkout(object.getPath());
        jcrMappingtemplate.update(object);
        jcrMappingtemplate.save();
        jcrMappingtemplate.checkin(object.getPath());
        //verify if necessary the save
        jcrMappingtemplate.save();
        jcrMappingtemplate.refresh(true);
        T returnObject = jcrMappingtemplate.getObject(object.getPath());
        returnObject.setVersion(jcrMappingtemplate.getBaseVersion(object.getPath()));
        return returnObject;
    }

    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#insert(it.av.youeat.ocm.model.BasicNode)
     */
    @Transactional
    public T insert(T object) throws YoueatException, UserAlreadyExistsException {  
        if(!(StringUtils.startsWith(object.getPath(), basePath))){
            object.setPath(basePath + object.getPath());    
        }
        jcrMappingtemplate.insert(object);
        jcrMappingtemplate.save();
        jcrMappingtemplate.checkout(object.getPath());
        jcrMappingtemplate.checkin(object.getPath());
        //verify if necessary the save
        jcrMappingtemplate.save();
        jcrMappingtemplate.refresh(true);
        T returnObject = jcrMappingtemplate.getObject(object.getPath());
        returnObject.setVersion(jcrMappingtemplate.getBaseVersion(object.getPath()));
        return returnObject;
    }
    
    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#getAll()
     */
    @Transactional(readOnly=true)
    public List<T> getAll() throws YoueatException{
        //FIXME doescn'work on the current session
        QueryManager queryManager = jcrMappingtemplate.createQueryManager();
        Filter filter = queryManager.createFilter(getPersistentClass());
        // scope ends with double slash // to search in all sub nodes and fields 
        filter.setScope(basePath + "//");
        Query query = queryManager.createQuery(filter);
        return new ArrayList<T>(jcrMappingtemplate.getObjects(query));
    }

    
    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#find(java.lang.String)
     */
    @Transactional(readOnly=true)
    public List<T> find(String pattern) throws YoueatException {
        QueryManager queryManager = this.jcrMappingtemplate.createQueryManager();
        Filter filter = queryManager.createFilter(getPersistentClass());
        // scope ends with double slash // to search in all sub nodes and fields 
        filter.setScope(basePath + "//");
        filter.addContains(".", pattern);
        Query query = queryManager.createQuery(filter);
        return new ArrayList<T>(jcrMappingtemplate.getObjects(query));
    }

    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#remove(it.av.youeat.ocm.model.BasicNode)
     */
    @Transactional
    public void remove(T object) throws YoueatException {
        jcrMappingtemplate.remove(object.getPath());
        jcrMappingtemplate.save();
        jcrMappingtemplate.refresh(true);
    }

    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#getAllRevisions(java.lang.String)
     */
    @Transactional(readOnly=true)
    public List<T> getAllRevisions(String path) throws YoueatException {
        ArrayList<T> revisions = new ArrayList<T>();
        if (StringUtils.isNotEmpty(path)) {
            VersionIterator versionIterator = jcrMappingtemplate.getAllRevisions(path);
            while (versionIterator.hasNext()) {
                Version version = (Version) versionIterator.next();
                if (version.getName().equals("jcr:rootVersion")) {
                    continue;
                }
                T versionObject = jcrMappingtemplate.getObjectByVersion(path, version.getName());
                versionObject.setVersion(version.getName());
                revisions.add(versionObject);
            }
        }
        //Sort to have latest release in first position
        Collections.reverse(revisions);
        return revisions;
    }

    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#getByPath(java.lang.String)
     */
    public T getByPath(String path) throws YoueatException {
        T object = jcrMappingtemplate.getObject(path);
        object.setVersion(jcrMappingtemplate.getBaseVersion(path));
        return object;
    }
    
    /* (non-Javadoc)
     * @see it.av.youeat.service.JcrApplicationService#getByUuid(java.lang.String)
     */
    @Override
    public T getByUuid(String uuid) throws YoueatException {
        Node node = jcrMappingtemplate.getNodeByUUID(uuid);
        try {
            return jcrMappingtemplate.getObject(node.getPath());
        } catch (RepositoryException e) {
           throw new YoueatException(e);
        }
    }

    
    public void setJcrMappingtemplate(JcrMappingTemplateGeneric<T> jcrMappingtemplate) {
        this.jcrMappingtemplate = jcrMappingtemplate;
    }
    
    public JcrMappingTemplateGeneric<T> getJcrMappingtemplate() {
        return jcrMappingtemplate;
    }

    public String getBasePath() {
        if(basePath == null){
            return "";
        }
        return basePath;
    }

    public void setBasePath(String basePath) {
        if(StringUtils.isBlank(basePath)){
            this.basePath = "";
        }
        else{
            this.basePath = StringUtils.trimToEmpty(basePath);
            if ((StringUtils.endsWith(this.basePath, "/"))){
                this.basePath = StringUtils.removeEnd(this.basePath, "/");
            }
        }
    }

    protected Class<T> getPersistentClass(){
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
