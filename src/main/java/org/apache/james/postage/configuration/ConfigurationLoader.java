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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.postage.user.UserList;

/**
 * generates named PostageConfigurations
 */
public class ConfigurationLoader {

    private static Log log = LogFactory.getLog(ConfigurationLoader.class);

    public Map<String, PostageConfiguration> create(Configuration configuration) {
        log.debug("reading configuration.");

        Map<String, PostageConfiguration> postageConfigurations = new LinkedHashMap<String, PostageConfiguration>();

        List<Object> scenariosIds = configuration.getList("scenario[@id]");
        log.debug("scenarios contained in configuration: " + scenariosIds.size());

        Iterator<Object> scenarioIter = scenariosIds.iterator();
        int scenarioCount = 0;
        while (scenarioIter.hasNext()) {
            String scenarioId = (String) scenarioIter.next();

            if (postageConfigurations.containsKey(scenarioId)) {
                log.error("found in configuration more than one scenario which is named: " + scenarioId);
                continue;
            }

            PostageConfiguration postageConfiguration = new PostageConfiguration(scenarioId);

            String scenario = getIndexedPropertyName("scenario", scenarioCount);

            postageConfiguration.setDurationMinutes(configuration.getInt(getAttributedPropertyName(scenario,
                    "runtimeMinutes")));

            addDescription(postageConfiguration, configuration.subset(scenario + ".description"));

            String scenarioInternalUsers = scenario + ".users.internal";
            UserList internals = new UserList(configuration.getInt(getAttributedPropertyName(scenarioInternalUsers,
                    "count")), configuration.getString(getAttributedPropertyName(scenarioInternalUsers,
                    "username-prefix")), configuration.getString(getAttributedPropertyName(scenarioInternalUsers,
                    "domain")), configuration.getString(getAttributedPropertyName(scenarioInternalUsers, "password")));
            postageConfiguration.setInternalUsers(internals);
            postageConfiguration.setInternalReuseExisting(configuration.getBoolean(getAttributedPropertyName(
                    scenarioInternalUsers, "reuseExisting")));

            String scenarioExternalUsers = scenario + ".users.external";
            UserList externals = new UserList(configuration.getInt(getAttributedPropertyName(scenarioExternalUsers,
                    "count")), configuration.getString(getAttributedPropertyName(scenarioExternalUsers,
                    "username-prefix")), configuration.getString(getAttributedPropertyName(scenarioExternalUsers,
                    "domain")));
            postageConfiguration.setExternalUsers(externals);

            String scenarioTestserver = scenario + ".testserver";
            postageConfiguration.setTestserverHost(configuration.getString(getAttributedPropertyName(
                    scenarioTestserver, "host")));
            postageConfiguration.setTestserverPortPOP3(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".pop3", "port")));
            postageConfiguration.setTestserverPOP3FetchesPerMinute(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".pop3", "count-per-min")));
            postageConfiguration.setTestserverPortSMTPInbound(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".smtp-inbound", "port")));
            postageConfiguration.setTestserverPortSMTPForwarding(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".smtp-forwarding", "port")));
            postageConfiguration.setTestserverSMTPForwardingWaitSeconds(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".smtp-forwarding", "latecomer-wait-seconds")));
            postageConfiguration.setTestserverRemoteManagerPort(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".remotemanager", "port")));
            postageConfiguration.setTestserverRemoteManagerUsername(configuration.getString(getAttributedPropertyName(
                    scenarioTestserver + ".remotemanager", "name")));
            postageConfiguration.setTestserverRemoteManagerPassword(configuration.getString(getAttributedPropertyName(
                    scenarioTestserver + ".remotemanager", "password")));
            postageConfiguration.setTestserverSpamAccountUsername(configuration.getString(getAttributedPropertyName(
                    scenarioTestserver + ".spam-account", "name")));
            postageConfiguration.setTestserverSpamAccountPassword(configuration.getString(getAttributedPropertyName(
                    scenarioTestserver + ".spam-account", "password")));
            postageConfiguration.setTestserverPortJMXRemoting(configuration.getInt(getAttributedPropertyName(
                    scenarioTestserver + ".jvm-resources", "jmx-remoting-port")));

            addSendProfiles(postageConfiguration, configuration, scenario);

            postageConfigurations.put(postageConfiguration.getId(), postageConfiguration);

            scenarioCount++;
        }

        return postageConfigurations;
    }

    private void addDescription(PostageConfiguration postageConfiguration, Configuration configuration) {
        Iterator<String> keys = configuration.getKeys();

        while (keys.hasNext()) {
            String itemName = keys.next();
            String itemContent = configuration.getString(itemName);

            postageConfiguration.addDescriptionItem(itemName, itemContent);
        }
    }

    private void addSendProfiles(PostageConfiguration postageConfiguration, Configuration configuration, String scenario) {
        List<Object> profileNames = configuration.getList(scenario + ".profiles.profile[@name]");
        log.debug("profiles contained in scenario " + postageConfiguration.getId() + ": " + profileNames.size());

        Iterator<Object> profileIter = profileNames.iterator();
        int profileCount = 0;
        while (profileIter.hasNext()) {
            String profileName = (String) profileIter.next();

            SendProfile profile = new SendProfile(profileName);

            String profilePath = getIndexedPropertyName(scenario + ".profiles.profile", profileCount);

            profile.setSourceInternal(convertToInternalExternalFlag(configuration.getString(getAttributedPropertyName(
                    profilePath, "source"))));
            profile.setTargetInternal(convertToInternalExternalFlag(configuration.getString(getAttributedPropertyName(
                    profilePath, "target"))));

            addMailSender(profile, configuration, profilePath);

            postageConfiguration.addProfile(profile);

            profileCount++;
        }
    }

    private void addMailSender(SendProfile profile, Configuration configuration, String profilePath) {
        List<Object> mailSenders = configuration.getList(profilePath + ".send[@count-per-min]");

        Iterator<Object> mailSenderIter = mailSenders.iterator();
        int mailSenderCount = 0;
        while (mailSenderIter.hasNext()) {
            mailSenderIter.next(); // ignore

            String mailSenderPath = getIndexedPropertyName(profilePath + ".send", mailSenderCount);

            MailSender mailSender = new MailSender(profile);
            mailSender.setSubject(configuration.getString(getAttributedPropertyName(mailSenderPath, "subject"),
                    "Apache JAMES Postage test mail"));
            mailSender.setSendPerMinute(configuration
                    .getInt(getAttributedPropertyName(mailSenderPath, "count-per-min")));
            mailSender.setSizeMinText(configuration.getInt(getAttributedPropertyName(mailSenderPath, "text-size-min"),
                    0));
            mailSender.setSizeMaxText(configuration.getInt(getAttributedPropertyName(mailSenderPath, "text-size-max"),
                    0));
            mailSender.setSizeMinBinary(configuration.getInt(
                    getAttributedPropertyName(mailSenderPath, "binary-size-min"), 0));
            mailSender.setSizeMaxBinary(configuration.getInt(
                    getAttributedPropertyName(mailSenderPath, "binary-size-max"), 0));
            mailSender.setMailFactoryClassname(configuration.getString(
                    getAttributedPropertyName(mailSenderPath, "mail-factory-class"), null));

            profile.addMailSender(mailSender);

            mailSenderCount++;
        }
    }

    private boolean convertToInternalExternalFlag(String flagCleartext) {
        return flagCleartext == null || !"extern".equals(flagCleartext.toLowerCase().trim());
    }

    private String getIndexedPropertyName(String name, int scenarioCount) {
        return name + "(" + scenarioCount + ")";
    }

    private String getAttributedPropertyName(String name, String attr) {
        return name + "[@" + attr + "]";
    }

}
