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
package org.apache.james.postage.smtpserver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.dnsservice.api.DNSService;
import org.apache.james.domainlist.api.mock.SimpleDomainList;
import org.apache.james.filesystem.api.FileSystem;
import org.apache.james.filesystem.api.mock.MockFileSystem;
import org.apache.james.mailrepository.api.MailRepositoryStore;
import org.apache.james.mailrepository.mock.MockMailRepositoryStore;
import org.apache.james.postage.SamplingException;
import org.apache.james.postage.execution.Sampler;
import org.apache.james.postage.result.PostageRunnerResult;
import org.apache.james.protocols.lib.mock.MockProtocolHandlerLoader;
import org.apache.james.queue.api.MailQueue;
import org.apache.james.queue.api.MailQueueFactory;
import org.apache.james.queue.api.mock.MockMailQueue;
import org.apache.james.queue.api.mock.MockMailQueueFactory;
import org.apache.james.rrt.api.RecipientRewriteTable;
import org.apache.james.rrt.api.RecipientRewriteTableException;
import org.apache.james.smtpserver.netty.SMTPServer;
import org.apache.james.user.api.UsersRepository;
import org.apache.james.user.lib.mock.MockUsersRepository;
import org.apache.mailet.HostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Puts up a gateway SMTP server acting as a mail sink for the external mail sent out by James.
 * </p>
 * <p>
 * Mails are catched, test mails are identified and tracked.
 * </p>
 */
public class SMTPMailSink implements Sampler {

    private static Log log = LogFactory.getLog(SMTPMailSink.class);

    private int smtpListenerPort = 2525;
    private SimpleMailServer mailServer = new SimpleMailServer();
    private SMTPServer smtpServer = new SMTPServer();

    protected HierarchicalConfiguration configuration;
    protected UsersRepository usersRepository = new MockUsersRepository();
    protected AlterableDNSServer dnsServer;
    protected MailRepositoryStore store;
    protected FileSystem fileSystem;
    protected DNSService dnsService;
    protected MockProtocolHandlerLoader chain;
    protected MailQueueFactory queueFactory;
    protected MailQueue queue;

    public int getSmtpListenerPort() {
        return this.smtpListenerPort;
    }

    public void setSmtpListenerPort(int smtpListenerPort) {
        this.smtpListenerPort = smtpListenerPort;
    }

    public void setResults(PostageRunnerResult results) {
        this.mailServer.setResults(results);
    }
    
    public void initialize() throws Exception {

        Logger log = LoggerFactory.getLogger("SMTP");

        chain = new MockProtocolHandlerLoader();

        chain.put("usersrepository", this.usersRepository);

        this.dnsServer = new AlterableDNSServer();
        chain.put("dnsservice", this.dnsServer);

        store = new MockMailRepositoryStore();
        chain.put("mailStore", store);
        fileSystem = new MockFileSystem();

        chain.put("filesystem", fileSystem);
        chain.put("org.apache.james.smtpserver.protocol.DNSService", dnsService);
        chain.put("recipientrewritetable", new RecipientRewriteTable() {
            @Override
            public void addRegexMapping(String user, String domain, String regex) throws RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void removeRegexMapping(String user, String domain, String regex) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void addAddressMapping(String user, String domain, String address) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void removeAddressMapping(String user, String domain, String address) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void addErrorMapping(String user, String domain, String error) throws RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void removeErrorMapping(String user, String domain, String error) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public Collection<String> getUserDomainMappings(String user, String domain) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void addMapping(String user, String domain, String mapping) throws RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void removeMapping(String user, String domain, String mapping) throws RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public Map<String, Collection<String>> getAllMappings() throws RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void addAliasDomainMapping(String aliasDomain, String realDomain) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public void removeAliasDomainMapping(String aliasDomain, String realDomain) throws
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
            @Override
            public Collection<String> getMappings(String user, String domain) throws ErrorMappingException,
                    RecipientRewriteTableException {
                throw new UnsupportedOperationException("Not implemented");
            }
        });

        chain.put("org.apache.james.smtpserver.protocol.DNSService", dnsService);
        queueFactory = new MockMailQueueFactory();
        queue = (MockMailQueue) queueFactory.getQueue(MockMailQueueFactory.SPOOL);
        chain.put("mailqueuefactory", queueFactory);
        chain.put("domainlist", new SimpleDomainList() {

            @Override
            public boolean containsDomain(String serverName) {
                return "localhost".equals(serverName);
            }
        });

        this.smtpServer = new SMTPServer();
        this.smtpServer.setDNSService(this.dnsServer);
        this.smtpServer.setFileSystem(fileSystem);

        this.smtpServer.setProtocolHandlerLoader(chain);

        this.smtpServer.setLog(log);
        
//        this.configuration = new SimpleSMTPServerConfiguration(getSmtpListenerPort());
//        this.smtpServer.configure(this.configuration);

        this.smtpServer.init();

    }

    protected void setUpFakeLoader() throws Exception {
    }

    public void doSample() throws SamplingException {
        log.debug("sampling while mails are coming in.");
    }
    
    final class AlterableDNSServer implements DNSService {
        private InetAddress localhostByName = null;
        @Override
        public Collection<String> findMXRecords(String hostname) {
            List<String> res = new ArrayList<String>();
            if (hostname == null) {
                return res;
            }
            if ("james.apache.org".equals(hostname)) {
                res.add("nagoya.apache.org");
            }
            return res;
        }
        public Iterator<HostAddress> getSMTPHostAddresses(String domainName) {
            throw new UnsupportedOperationException("Unimplemented mock service");
        }
        @Override
        public InetAddress[] getAllByName(String host) throws UnknownHostException {
            return new InetAddress[]{getByName(host)};
        }
        @Override
        public InetAddress getByName(String host) throws UnknownHostException {
            if (getLocalhostByName() != null) {
                if ("127.0.0.1".equals(host)) {
                    return getLocalhostByName();
                }
            }
            if ("1.0.0.127.bl.spamcop.net.".equals(host)) {
                return InetAddress.getByName("localhost");
            }
            if ("james.apache.org".equals(host)) {
                return InetAddress.getByName("james.apache.org");
            }
            if ("abgsfe3rsf.de".equals(host)) {
                throw new UnknownHostException();
            }
            if ("128.0.0.1".equals(host) || "192.168.0.1".equals(host) || "127.0.0.1".equals(host) || "127.0.0.0".equals(
                    host) || "255.0.0.0".equals(host) || "255.255.255.255".equals(host)) {
                return InetAddress.getByName(host);
            }
            throw new UnsupportedOperationException("getByName not implemented in mock for host: " + host);
        }
        @Override
        public Collection<String> findTXTRecords(String hostname) {
            List<String> res = new ArrayList<String>();
            if (hostname == null) {
                return res;
            }
            if ("2.0.0.127.bl.spamcop.net.".equals(hostname)) {
                res.add("Blocked - see http://www.spamcop.net/bl.shtml?127.0.0.2");
            }
            return res;
        }
        public InetAddress getLocalhostByName() {
            return localhostByName;
        }
        public void setLocalhostByName(InetAddress localhostByName) {
            this.localhostByName = localhostByName;
        }
        @Override
        public String getHostName(InetAddress addr) {
            return addr.getHostName();
        }
        @Override
        public InetAddress getLocalHost() throws UnknownHostException {
            return InetAddress.getLocalHost();
        }
    }

}
