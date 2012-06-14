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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collection of all users used for one Postage scenario
 */
public class UserList {
    int count = 0;
    String namePrefix = null;
    List<String>   users    = new ArrayList<String>();
    String password = null; // common to all users, if set
    String domain   = null; // domain, common to all users

    public UserList(int count, String namePrefix, String domain) {
        this.count = count;
        this.namePrefix = namePrefix;
        this.domain   = domain;
    }

    public UserList(int count, String namePrefix, String domain, String password) {
        this(count, namePrefix, domain);
        this.password = password;
    }

    public int getCount() {
        return this.count;
    }

    public String getNamePrefix() {
        return this.namePrefix;
    }

    public Iterator<String> getUsernames() {
        return this.users.iterator();
    }

    public void setExistingUsers(List<String> existingUsers) {
        this.users.clear();
        this.users.addAll(existingUsers);
    }

    public String getPassword() {
        return this.password;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getRandomUsername() {
        if (this.users.isEmpty()) return null;
        return this.users.get((int)(Math.random() * (this.users.size() - 1)));
    }

    public String getEmailAddress(String username) {
        return username + "@" + this.domain;
    }

}
