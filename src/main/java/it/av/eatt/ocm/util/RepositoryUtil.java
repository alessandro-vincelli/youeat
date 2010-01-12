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
package it.av.eatt.ocm.util;

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


import it.av.eatt.YoueatException;

import javax.jcr.Session;

import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;

/**
 * Utility class for managing JCR repositories. <b>Note</b>: most of the utility
 * methods in this class can be used only with Jackrabbit.
 * 
 */
public final class RepositoryUtil {

    private RepositoryUtil() {
    };

    /** namespace prefix constant */
    public static final String OCM_NAMESPACE_PREFIX = "ocm";

    /** namespace constant */
    public static final String OCM_NAMESPACE = "http://jackrabbit.apache.org/ocm";

    /** Item path separator */
    public static final String PATH_SEPARATOR = "/";

    /**
     * Setup the session. Until now, we check only if the namespace prefix exist
     * in the repository
     * 
     * @throws YoueatException
     * 
     */
    public static void setupSession(Session session) throws YoueatException {
        try {

            String[] jcrNamespaces = session.getWorkspace().getNamespaceRegistry().getPrefixes();
            boolean createNamespace = true;
            for (int i = 0; i < jcrNamespaces.length; i++) {
                if (jcrNamespaces[i].equals(OCM_NAMESPACE_PREFIX)) {
                    createNamespace = false;
                }
            }

            if (createNamespace) {
                session.getWorkspace().getNamespaceRegistry().registerNamespace(OCM_NAMESPACE_PREFIX, OCM_NAMESPACE);
            }

        } catch (Exception e) {
            throw new YoueatException(e);
        }
    }

    /**
     * Encode a path
     * 
     * TODO : drop Jackrabbit dependency
     * 
     * @param path
     *            the path to encode
     * @return the encoded path
     * 
     */
    public static String encodePath(String path) {
        String[] pathElements = Text.explode(path, '/');
        for (int i = 0; i < pathElements.length; i++) {
            pathElements[i] = ISO9075.encode(pathElements[i]);
        }
        return "/" + Text.implode(pathElements, "/");
    }
}
