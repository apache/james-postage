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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.james.mailrepository.api.MailRepository;
import org.apache.james.postage.result.PostageRunnerResult;
import org.apache.james.smtpserver.netty.SMTPServer;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;

/**
 * <p>
 * A quite simple (only receiving) {@link SMTPServer} which reads mails and tries to match 
 * them with sent test mails. It reuses James' own SMTP server components.
 * </p>
 */
public class SimpleMailServer {

    private int counter = 0;
    private PostageRunnerResult results;

    public void sendMail(MailAddress sender, Collection recipients, MimeMessage message) throws MessagingException {
        try {
            new SMTPMailAnalyzeStrategy("smtpOutbound", this.results, message).handle();
        } catch (Exception e) {
            throw new MessagingException("error handling message", e);
        }
    }

    public void sendMail(MailAddress sender, Collection recipients, InputStream msg) throws MessagingException {
        //Object[] mailObjects = new Object[]{sender, recipients, msg};
        throw new IllegalStateException("not supported");
    }

    public void sendMail(Mail mail) throws MessagingException {
        sendMail(mail.getSender(), mail.getRecipients(), mail.getMessage());
    }

    public void sendMail(MimeMessage message) throws MessagingException {
        // taken from class org.apache.james.James
        MailAddress sender = new MailAddress((InternetAddress)message.getFrom()[0]);
        Collection<MailAddress> recipients = new HashSet<MailAddress>();
        Address addresses[] = message.getAllRecipients();
        if (addresses != null) {
            for (int i = 0; i < addresses.length; i++) {
                // Javamail treats the "newsgroups:" header field as a
                // recipient, so we want to filter those out.
                if ( addresses[i] instanceof InternetAddress ) {
                    recipients.add(new MailAddress((InternetAddress)addresses[i]));
                }
            }
        }
        sendMail(sender, recipients, message);
    }

    public MailRepository getUserInbox(String userName) {
        throw new IllegalStateException("not implemented");
    }

    public synchronized String getId() {
        this.counter++;
        return "SimpleMailServer-ID-" + this.counter;
    }

    public boolean addUser(String userName, String password) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isLocalServer(String serverName) {
        return true;
    }

    public void setResults(PostageRunnerResult results) {
        this.results = results;
    }

    public String getDefaultDomain() {
        return "localhost";
    }

    public String getHelloName() {
        return "localhost";
    }

    public boolean supportVirtualHosting() {
        return false;
    }
}

