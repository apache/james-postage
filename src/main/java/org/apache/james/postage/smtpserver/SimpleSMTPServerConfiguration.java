/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/


package org.apache.james.postage.smtpserver;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.Configuration;

public class SimpleSMTPServerConfiguration extends DefaultConfiguration {
    private int m_smtpListenerPort;
    private String m_authorizedAddresses = "127.0.0.0/8";
    private String m_authorizingMode = "false";

    public static Configuration getValuedConfiguration(String name, String value) {
        DefaultConfiguration defaultConfiguration = new DefaultConfiguration(name);
        defaultConfiguration.setValue(value);
        return defaultConfiguration;
    }

    public SimpleSMTPServerConfiguration(int smtpListenerPort) {
        super("smptserver");

        m_smtpListenerPort = smtpListenerPort;
    }

    public String getAuthorizedAddresses() {
        return m_authorizedAddresses;
    }

    public void setAuthorizedAddresses(String authorizedAddresses) {
        m_authorizedAddresses = authorizedAddresses;
    }

    public void setAuthorizingNotRequired() {
        m_authorizingMode = "false";
    }

    public void setAuthorizingRequired() {
        m_authorizingMode = "true";
        //m_verifyIdentity = true;
    }

    public void setAuthorizingAnnounce() {
        m_authorizingMode = "announce";
        //m_verifyIdentity = true;
    }

    public void init() {

        setAttribute("enabled", true);

        addChild(getValuedConfiguration("port", "" + m_smtpListenerPort));

        DefaultConfiguration handlerConfig = new DefaultConfiguration("handler");
        handlerConfig.addChild(getValuedConfiguration("helloName", "myMailServer"));
        handlerConfig.addChild(getValuedConfiguration("connectiontimeout", "360000"));
        handlerConfig.addChild(getValuedConfiguration("authorizedAddresses", m_authorizedAddresses));
        handlerConfig.addChild(getValuedConfiguration("maxmessagesize", "" + 0));
        handlerConfig.addChild(getValuedConfiguration("authRequired", m_authorizingMode));

        addChild(handlerConfig);
    }

}
