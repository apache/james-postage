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

import org.apache.james.postage.result.PostageRunnerResult;
import org.apache.james.services.MailRepository;
import org.apache.james.services.MailServer;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;


/**
 * a quite simple (only receiving) SMTP server which reads mails and tries to match them with sent test mails.<br/>
 * reuses James' own SMTP server components
 */
public class SimpleMailServer implements MailServer {

    private int m_counter = 0;
    private PostageRunnerResult m_results;

    public void sendMail(MailAddress sender, Collection recipients, MimeMessage message) throws MessagingException {
        try {
            new SMTPMailAnalyzeStrategy("smtpOutbound", m_results, message).handle();
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
        Collection recipients = new HashSet();
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
        m_counter++;
        return "SimpleMailServer-ID-" + m_counter;
    }

    public boolean addUser(String userName, String password) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isLocalServer(String serverName) {
        return true;
    }

    public void setResults(PostageRunnerResult results) {
        m_results = results;
    }

    /* JAMES 3.0-SNAPSHOT specific methods */
    
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

