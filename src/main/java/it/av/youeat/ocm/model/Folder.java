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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.NTCollectionConverterImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrType = "nt:unstructured", jcrMixinTypes = "mix:versionable, mix:lockable")
public class Folder implements BasicNode, Serializable{
    
        @Collection(proxy=true, autoUpdate=false, autoInsert=false, autoRetrieve=false,
                    elementClassName=BasicNode.class,
                    collectionConverter=NTCollectionConverterImpl.class)
        protected List children = new ArrayList();
        @Field(id=true, jcrMandatory=true)
        protected String name;
        @Field(path = true)
        private String path;

        public List getChildren() {
            return children;
        }

        public void setChildren(List children) {
            this.children = children;
        }

        public void addChild(BasicNode child)
        {
                children.add(child);
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getVersion() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public void setVersion(String version) {
            // TODO Auto-generated method stub
            
        }

        public final String getName() {
            return name;
        }

        public final void setName(String name) {
            this.name = name;
        }
        
}