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


package org.apache.james.postage.client;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.james.postage.PostageException;
import org.apache.james.postage.SamplingException;
import org.apache.james.postage.StartupException;
import org.apache.james.postage.execution.Sampler;
import org.apache.james.postage.result.PostageRunnerResult;
import org.apache.james.postage.user.UserList;

/**
 * acts like a MUA using POP3 protocol.<br/>
 * fetches all mails for one (all) users and initiates adding to results
 */
public class POP3Client implements Sampler {

    private static Log log = LogFactory.getLog(POP3Client.class);

    private String m_host;
    private int m_port;
    private UserList m_internalUsers;
    private PostageRunnerResult m_results;

    public POP3Client(String host, int port, UserList internalUsers, PostageRunnerResult results) {
        m_host = host;
        m_port = port;
        m_internalUsers = internalUsers;
        m_results = results;
    }

    /**
     * checks, if the configured POP3 services is accessable
     */
    public boolean checkAvailability() throws StartupException {
        try {
            org.apache.commons.net.pop3.POP3Client pop3Client = openConnection(m_internalUsers.getRandomUsername());
            closeSession(pop3Client);
        } catch (PostageException e) {
            throw new StartupException("error checking availability");
        }
        return true;
    }

    private void closeSession(org.apache.commons.net.pop3.POP3Client pop3Client) throws PostageException {
        try {
            pop3Client.sendCommand("QUIT");
            pop3Client.disconnect();
        } catch (IOException e) {
            throw new PostageException("error closing pop3 session", e);
        }
    }

    private org.apache.commons.net.pop3.POP3Client openConnection(String username) throws PostageException {
        org.apache.commons.net.pop3.POP3Client pop3Client = new org.apache.commons.net.pop3.POP3Client();
        try {
            pop3Client.connect(m_host, m_port);
            pop3Client.login(username, m_internalUsers.getPassword());
        } catch (IOException e) {
            throw new PostageException("POP3 service not available", e);
        }
        return pop3Client;
    }

    /**
     * take one POP3 sample for a random user
     */
    public void doSample() throws SamplingException {
        String username = m_internalUsers.getRandomUsername();

        try {
            findAllMatchingTestMail(username);
        } catch (SamplingException e) {
            log.warn("error sampling mail for user " + username);
            throw e;
        }
    }

    /**
     * used after completing with regular test scenario. tries to collect all mails, which are left 
     * unprocessed by the random access. this is done by iterating over all user accounts, looking for mail
     */
    public void doMatchMailForAllUsers() {
        Iterator usernames = m_internalUsers.getUsernames();
        while (usernames.hasNext()) {
            String username = (String)usernames.next();
            try {
                findAllMatchingTestMail(username);
            } catch (SamplingException e) {
                log.warn("error reading mail for user " + username);
            }
        }
    }

    /**
     * for the specified user, fetches all mail and invokes the matching process
     * @param username
     * @throws SamplingException
     */
    private void findAllMatchingTestMail(String username) throws SamplingException {
        try {
            org.apache.commons.net.pop3.POP3Client pop3Client = openConnection(username);

            // retrieve all messages
            POP3MessageInfo[] entries = null;
            try {
                entries = pop3Client.listMessages();
            } catch (Exception e) {
                String errorMessage = "failed to read pop3 account mail list for " + username;
                m_results.addError(500, errorMessage);
                log.info(errorMessage);
                return;
            }

            for (int i = 0; entries != null && i < entries.length; i++) {
                POP3MessageInfo entry = entries[i];

                try {
                    new POP3MailAnalyzeStrategy("pop3", m_results, pop3Client, entry.number, i).handle();
                } catch (Exception exception) {
                    log.warn("error processing pop3 mail", exception);
                }
            }

            closeSession(pop3Client);
        } catch (PostageException e) {
            throw new SamplingException("sample failed", e);
        }
    }
}

