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

import org.apache.james.postage.user.UserList;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostageConfiguration {
    private String m_id;

    private int     m_durationMinutes = 10;

    private boolean m_internalReuseExisting = true;

    private Map<String, String>     m_descriptionItems = new LinkedHashMap<String, String>();

    private UserList m_externalUsers = null;

    private UserList m_internalUsers = null;

    private String  m_testserverHost = null;

    private int     m_testserverPortSMTPForwarding = -1;

    private int     m_testserverSMTPForwardingWaitSeconds = 0;

    private int     m_testserverPortSMTPInbound = -1;

    private int     m_testserverPortPOP3 = -1;

    private int     m_testserverPOP3FetchesPerMinute = 1;

    private int     m_testserverRemoteManagerPort = -1;

    private String  m_testserverRemoteManagerUsername = null;

    private String  m_testserverRemoteManagerPassword = null;

    private String  m_testserverSpamAccountUsername = null;

    private String  m_testserverSpamAccountPassword = null;

    private int     m_testserverJMXRemotingPort = -1;

    private List<SendProfile>    m_profiles = new ArrayList<SendProfile>();

    public PostageConfiguration(String id) {
        m_id = id;
    }

    public String getId() {
        return m_id;
    }

    public int getDurationMinutes() {
        return m_durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        m_durationMinutes = durationMinutes;
    }

    public boolean isInternalReuseExisting() {
        return m_internalReuseExisting;
    }

    public void addDescriptionItem(String name, String value) {
        m_descriptionItems.put(name, value);
    }

    public Map<String, String> getDescriptionItems() {
        return Collections.unmodifiableMap(m_descriptionItems);
    }

    public void setInternalReuseExisting(boolean internalReuseExisting) {
        m_internalReuseExisting = internalReuseExisting;
    }

    public UserList getExternalUsers() {
        return m_externalUsers;
    }

    public void setExternalUsers(UserList externalUsers) {
        m_externalUsers = externalUsers;
    }

    public UserList getInternalUsers() {
        return m_internalUsers;
    }

    public void setInternalUsers(UserList internalUsers) {
        m_internalUsers = internalUsers;
    }

    public String getTestserverHost() {
        return m_testserverHost;
    }

    public void setTestserverHost(String testserverHost) {
        m_testserverHost = testserverHost;
    }

    public int getTestserverPortSMTPForwarding() {
        return m_testserverPortSMTPForwarding;
    }

    public void setTestserverPortSMTPForwarding(int testserverPortSMTPForwarding) {
        m_testserverPortSMTPForwarding = testserverPortSMTPForwarding;
    }

    public int getTestserverSMTPForwardingWaitSeconds() {
        return m_testserverSMTPForwardingWaitSeconds;
    }

    public void setTestserverSMTPForwardingWaitSeconds(int testserverSMTPForwardingWaitSeconds) {
        m_testserverSMTPForwardingWaitSeconds = testserverSMTPForwardingWaitSeconds;
    }

    public int getTestserverPortSMTPInbound() {
        return m_testserverPortSMTPInbound;
    }

    public void setTestserverPortSMTPInbound(int testserverPortSMTPInbound) {
        m_testserverPortSMTPInbound = testserverPortSMTPInbound;
    }

    public int getTestserverPortPOP3() {
        return m_testserverPortPOP3;
    }

    public void setTestserverPortPOP3(int testserverPortPOP3) {
        m_testserverPortPOP3 = testserverPortPOP3;
    }

    public int getTestserverPOP3FetchesPerMinute() {
        return m_testserverPOP3FetchesPerMinute;
    }

    public void setTestserverPOP3FetchesPerMinute(int testserverPOP3FetchesPerMinute) {
        m_testserverPOP3FetchesPerMinute = testserverPOP3FetchesPerMinute;
    }

    public int getTestserverRemoteManagerPort() {
        return m_testserverRemoteManagerPort;
    }

    public void setTestserverRemoteManagerPort(int testserverRemoteManagerPort) {
        m_testserverRemoteManagerPort = testserverRemoteManagerPort;
    }

    public String getTestserverRemoteManagerUsername() {
        return m_testserverRemoteManagerUsername;
    }

    public void setTestserverRemoteManagerUsername(String testserverRemoteManagerUsername) {
        m_testserverRemoteManagerUsername = testserverRemoteManagerUsername;
    }

    public String getTestserverRemoteManagerPassword() {
        return m_testserverRemoteManagerPassword;
    }

    public void setTestserverRemoteManagerPassword(String testserverRemoteManagerPassword) {
        m_testserverRemoteManagerPassword = testserverRemoteManagerPassword;
    }

    public String getTestserverSpamAccountUsername() {
        return m_testserverSpamAccountUsername;
    }

    public void setTestserverSpamAccountUsername(String testserverSpamAccountUsername) {
        m_testserverSpamAccountUsername = testserverSpamAccountUsername;
    }

    public String getTestserverSpamAccountPassword() {
        return m_testserverSpamAccountPassword;
    }

    public void setTestserverSpamAccountPassword(String testserverSpamAccountPassword) {
        m_testserverSpamAccountPassword = testserverSpamAccountPassword;
    }

    public int getTestserverPortJMXRemoting() {
        return m_testserverJMXRemotingPort;
    }

    public void setTestserverPortJMXRemoting(int testserverJMXRemotingPort) {
        m_testserverJMXRemotingPort = testserverJMXRemotingPort;
    }

    public void addProfile(SendProfile profile) {
        m_profiles.add(profile);
    }

    public List<SendProfile> getProfiles() {
        return Collections.unmodifiableList(m_profiles);
    }

    public SendProfile findProfile(boolean sourceInternal, boolean targetInternal) {
        Iterator<SendProfile> iterator = m_profiles.iterator();
        while (iterator.hasNext()) {
            SendProfile sendProfile = iterator.next();
            if (sendProfile.isSourceInternal() == sourceInternal && sendProfile.isTargetInternal() == targetInternal) {
                return sendProfile;
            }
        }
        return null;
    }

    public int getTotalMailsPerMin() {
        Iterator<SendProfile> iterator = m_profiles.iterator();
        int total = 0;
        while (iterator.hasNext()) {
            SendProfile sendProfile = iterator.next();
            total += sendProfile.getTotalMailsPerMin();
        }
        return total;
    }
}
