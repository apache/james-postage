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
package org.apache.james.postage;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.james.postage.configuration.ConfigurationLoader;
import org.apache.james.postage.configuration.PostageConfiguration;

import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * bootstrapping the application
 */
public class Main {
    private static PostageRunner m_currentPostageRunner = null;

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please provide the configuration file");
            return;
        }

        String filename = args[0];

        List scenariosToRun = new ArrayList();
        for (int i = 1; i < args.length; i++)
        {
            scenariosToRun.add(args[i]);
        }

        // load all scenarios from configuration file
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Map configurations;
        try {
            // TODO allow different (non-xml) configs - as Common-Configuration supports it
            XMLConfiguration xmlConfiguration = new XMLConfiguration(filename);
            //xmlConfiguration.setThrowExceptionOnMissing(false);
            configurations = configurationLoader.create(xmlConfiguration);
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return;
        }

        // register shutdown hook if this app is terminated from outside
        Runtime.getRuntime().addShutdownHook(new Thread(){public void run() {shutdown();}});

        // run all scenarios
        runScenarios(configurations, scenariosToRun);
    }

    private static void runScenarios(Map configurations, List scenariosToRun) {
        // run all scenarios which are requested to be run.
        int playedScenarioCounter = 0;
        Iterator iterator = configurations.keySet().iterator();
        while (iterator.hasNext()) {
            String id = (String)iterator.next();
            // if no scenario is given on the command line, all get executed
            // if one or more is given, all others are skipped
            if (!scenariosToRun.isEmpty() && !scenariosToRun.contains(id)) continue;

            playedScenarioCounter++;
            PostageConfiguration postageConfiguration = (PostageConfiguration) configurations.get(id);
            m_currentPostageRunner = new PostageRunner(postageConfiguration);
            m_currentPostageRunner.run(); // TODO maybe we want to run this Runnable in its own thread, but maybe not
        }
        m_currentPostageRunner = null;
        if (playedScenarioCounter == 0) {
            System.out.println("No scenario has been executed. ");
            System.out.println("Either those on the command line where not matching those in the file.");
            System.out.println("Or the configuration file is empty");
        }
    }

    private static void shutdown() {
        if (m_currentPostageRunner != null) m_currentPostageRunner.terminate();
    }

}
