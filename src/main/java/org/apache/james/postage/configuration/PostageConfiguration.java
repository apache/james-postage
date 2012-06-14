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

package org.apache.james.postage.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.james.postage.user.UserList;

public class PostageConfiguration {
    private String id;

    private int durationMinutes = 10;

    private boolean internalReuseExisting = true;

    private Map<String, String> descriptionItems = new LinkedHashMap<String, String>();

    private UserList externalUsers = null;

    private UserList internalUsers = null;

    private String testserverHost = null;

    private int testserverPortSMTPForwarding = -1;

    private int testserverSMTPForwardingWaitSeconds = 0;

    private int testserverPortSMTPInbound = -1;

    private int testserverPortPOP3 = -1;

    private int testserverPOP3FetchesPerMinute = 1;

    private int testserverRemoteManagerPort = -1;

    private String testserverRemoteManagerUsername = null;

    private String testserverRemoteManagerPassword = null;

    private String testserverSpamAccountUsername = null;

    private String testserverSpamAccountPassword = null;

    private int testserverJMXRemotingPort = -1;

    private List<SendProfile> profiles = new ArrayList<SendProfile>();

    public PostageConfiguration(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isInternalReuseExisting() {
        return this.internalReuseExisting;
    }

    public void addDescriptionItem(String name, String value) {
        this.descriptionItems.put(name, value);
    }

    public Map<String, String> getDescriptionItems() {
        return Collections.unmodifiableMap(this.descriptionItems);
    }

    public void setInternalReuseExisting(boolean internalReuseExisting) {
        this.internalReuseExisting = internalReuseExisting;
    }

    public UserList getExternalUsers() {
        return this.externalUsers;
    }

    public void setExternalUsers(UserList externalUsers) {
        this.externalUsers = externalUsers;
    }

    public UserList getInternalUsers() {
        return this.internalUsers;
    }

    public void setInternalUsers(UserList internalUsers) {
        this.internalUsers = internalUsers;
    }

    public String getTestserverHost() {
        return this.testserverHost;
    }

    public void setTestserverHost(String testserverHost) {
        this.testserverHost = testserverHost;
    }

    public int getTestserverPortSMTPForwarding() {
        return this.testserverPortSMTPForwarding;
    }

    public void setTestserverPortSMTPForwarding(int testserverPortSMTPForwarding) {
        this.testserverPortSMTPForwarding = testserverPortSMTPForwarding;
    }

    public int getTestserverSMTPForwardingWaitSeconds() {
        return this.testserverSMTPForwardingWaitSeconds;
    }

    public void setTestserverSMTPForwardingWaitSeconds(
            int testserverSMTPForwardingWaitSeconds) {
        this.testserverSMTPForwardingWaitSeconds = testserverSMTPForwardingWaitSeconds;
    }

    public int getTestserverPortSMTPInbound() {
        return this.testserverPortSMTPInbound;
    }

    public void setTestserverPortSMTPInbound(int testserverPortSMTPInbound) {
        this.testserverPortSMTPInbound = testserverPortSMTPInbound;
    }

    public int getTestserverPortPOP3() {
        return this.testserverPortPOP3;
    }

    public void setTestserverPortPOP3(int testserverPortPOP3) {
        this.testserverPortPOP3 = testserverPortPOP3;
    }

    public int getTestserverPOP3FetchesPerMinute() {
        return this.testserverPOP3FetchesPerMinute;
    }

    public void setTestserverPOP3FetchesPerMinute(
            int testserverPOP3FetchesPerMinute) {
        this.testserverPOP3FetchesPerMinute = testserverPOP3FetchesPerMinute;
    }

    public int getTestserverRemoteManagerPort() {
        return this.testserverRemoteManagerPort;
    }

    public void setTestserverRemoteManagerPort(int testserverRemoteManagerPort) {
        this.testserverRemoteManagerPort = testserverRemoteManagerPort;
    }

    public String getTestserverRemoteManagerUsername() {
        return this.testserverRemoteManagerUsername;
    }

    public void setTestserverRemoteManagerUsername(
            String testserverRemoteManagerUsername) {
        this.testserverRemoteManagerUsername = testserverRemoteManagerUsername;
    }

    public String getTestserverRemoteManagerPassword() {
        return this.testserverRemoteManagerPassword;
    }

    public void setTestserverRemoteManagerPassword(
            String testserverRemoteManagerPassword) {
        this.testserverRemoteManagerPassword = testserverRemoteManagerPassword;
    }

    public String getTestserverSpamAccountUsername() {
        return this.testserverSpamAccountUsername;
    }

    public void setTestserverSpamAccountUsername(
            String testserverSpamAccountUsername) {
        this.testserverSpamAccountUsername = testserverSpamAccountUsername;
    }

    public String getTestserverSpamAccountPassword() {
        return this.testserverSpamAccountPassword;
    }

    public void setTestserverSpamAccountPassword(
            String testserverSpamAccountPassword) {
        this.testserverSpamAccountPassword = testserverSpamAccountPassword;
    }

    public int getTestserverPortJMXRemoting() {
        return this.testserverJMXRemotingPort;
    }

    public void setTestserverPortJMXRemoting(int testserverJMXRemotingPort) {
        this.testserverJMXRemotingPort = testserverJMXRemotingPort;
    }

    public void addProfile(SendProfile profile) {
        this.profiles.add(profile);
    }

    public List<SendProfile> getProfiles() {
        return Collections.unmodifiableList(this.profiles);
    }

    public SendProfile findProfile(boolean sourceInternal,
            boolean targetInternal) {
        Iterator<SendProfile> iterator = this.profiles.iterator();
        while (iterator.hasNext()) {
            SendProfile sendProfile = iterator.next();
            if (sendProfile.isSourceInternal() == sourceInternal
                    && sendProfile.isTargetInternal() == targetInternal) {
                return sendProfile;
            }
        }
        return null;
    }

    public int getTotalMailsPerMin() {
        Iterator<SendProfile> iterator = this.profiles.iterator();
        int total = 0;
        while (iterator.hasNext()) {
            SendProfile sendProfile = iterator.next();
            total += sendProfile.getTotalMailsPerMin();
        }
        return total;
    }
}
