/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.postage.classloading;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachedInstanceFactory {

    private static Log log = LogFactory.getLog(CachedInstanceFactory.class);

    private final static Map m_classes = new HashMap();

    public static Object createInstance(String classname) {
        Object object = null;

        Class clazz = null;
        // class is configured, but not yet loaded
        if (classname != null && m_classes.get(classname) == null) {
            try {
                clazz = Class.forName(classname);
            } catch (ClassNotFoundException e) {
                log.error("failed to load class " + classname, e);
            }
        }

        // create instance, if custom class is given
        if (clazz != null) {
            try {
                object = clazz.newInstance();
            } catch (Exception e) {
                log.error("failed to create instance of class " + classname, e);
            }
        }

        return object;
    }

}
