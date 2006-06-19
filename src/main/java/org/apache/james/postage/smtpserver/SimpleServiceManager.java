/***********************************************************************
 * Copyright (c) 2006 The Apache Software Foundation.                  *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/


package org.apache.james.postage.smtpserver;

import org.apache.avalon.framework.service.ServiceException;

import java.util.HashMap;

/**
 * mock-up of ServiceManager
 */
public class SimpleServiceManager implements org.apache.avalon.framework.service.ServiceManager {

    private HashMap m_serviceMap = new HashMap();

    public Object lookup(String serviceName) throws ServiceException {
        return m_serviceMap.get(serviceName);
    }

    public boolean hasService(String serviceName) {
        return m_serviceMap.get(serviceName) != null;
    }

    public void put(String name, Object service) {
        m_serviceMap.put(name, service);
    }

    public void release(Object object) {
         // trivial implementation
    }
}
