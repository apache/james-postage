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


package org.apache.james.postage.user;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * collection of all users used for one Postage scenario
 */
public class UserList {

    int m_count         = 0;
    String m_namePrefix = null;

    List<String>   m_users    = new ArrayList<String>();
    String m_password = null; // common to all users, if set
    String m_domain   = null; // domain, common to all users

    public UserList(int count, String namePrefix, String domain) {
        m_count = count;
        m_namePrefix = namePrefix;
        m_domain   = domain;
    }

    public UserList(int count, String namePrefix, String domain, String password) {
        this(count, namePrefix, domain);
        m_password = password;
    }

    public int getCount() {
        return m_count;
    }

    public String getNamePrefix() {
        return m_namePrefix;
    }

    public Iterator<String> getUsernames() {
        return m_users.iterator();
    }

    public void setExistingUsers(List<String> existingUsers) {
        m_users.clear();
        m_users.addAll(existingUsers);
    }

    public String getPassword() {
        return m_password;
    }

    public String getDomain() {
        return m_domain;
    }

    public String getRandomUsername() {
        if (m_users.isEmpty()) return null;
        return m_users.get((int)(Math.random() * (m_users.size() - 1)));
    }

    public String getEmailAddress(String username) {
        return username + "@" + m_domain;
    }
}
