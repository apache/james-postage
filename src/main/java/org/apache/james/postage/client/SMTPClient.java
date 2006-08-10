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

import org.apache.james.postage.SamplingException;
import org.apache.james.postage.StartupException;
import org.apache.james.postage.configuration.MailSender;
import org.apache.james.postage.execution.Sampler;
import org.apache.james.postage.result.MailProcessingRecord;
import org.apache.james.postage.result.PostageRunnerResult;
import org.apache.james.postage.user.UserList;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

/**
 * connects as a SMTP client and handles all mail according to its configuration.
 * it is threadsafe and reentrant and thus can be reused over multiple parallel client session
 */
public class SMTPClient implements Sampler {

    private String m_host;
    private int m_port;
    private UserList m_internalUsers;
    private UserList m_externalUsers;
    private PostageRunnerResult m_results;
    private MailSender m_mailSender;

    public SMTPClient(String host, int port, UserList internalUsers, UserList externalUsers, PostageRunnerResult results, MailSender mailSender) {
        m_host = host;
        m_port = port;
        m_internalUsers = internalUsers;
        m_externalUsers = externalUsers;
        m_mailSender = mailSender;
        m_results = results;
    }

    public boolean checkAvailability() throws StartupException {
        try {

            MailProcessingRecord proformaMailProcessingRecord = new MailProcessingRecord();
            Session session = getMailSession();
            Message message = m_mailSender.createMail(session, proformaMailProcessingRecord);
            setMailFromAndTo(message, proformaMailProcessingRecord);
            Transport.send(message);
        } catch (Exception e) {
            throw new StartupException("inbound SMTP service not available", e);
        }
        return true;
    }

    private void setMailFromAndTo(Message message, MailProcessingRecord mailProcessingRecord) throws MessagingException {

        String senderUsername;
        String senderMailAddress;
        if (m_mailSender.getParentProfile().isSourceInternal()) {
            senderUsername = m_internalUsers.getRandomUsername();
        } else {
            senderUsername = m_externalUsers.getRandomUsername();
        }
        if (m_mailSender.getParentProfile().isSourceInternal()) {
            senderMailAddress = m_internalUsers.getEmailAddress(senderUsername);
        } else {
            senderMailAddress = m_externalUsers.getEmailAddress(senderUsername);
        }
        mailProcessingRecord.setSender(senderUsername);
        mailProcessingRecord.setSenderMailAddress(senderMailAddress);
        message.setFrom(new InternetAddress(senderMailAddress));

        String recepientUsername;
        String recepientMailAddress;
        if (m_mailSender.getParentProfile().isTargetInternal()) {
            recepientUsername = m_internalUsers.getRandomUsername();
        } else {
            recepientUsername = m_externalUsers.getRandomUsername();
        }
        if (m_mailSender.getParentProfile().isTargetInternal()) {
            recepientMailAddress = m_internalUsers.getEmailAddress(recepientUsername);
        } else {
            recepientMailAddress = m_externalUsers.getEmailAddress(recepientUsername);
        }
        mailProcessingRecord.setReceiver(recepientUsername);
        mailProcessingRecord.setReceiverMailAddress(recepientMailAddress);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepientMailAddress));
    }

    public synchronized void doSample() throws SamplingException {

        MailProcessingRecord mailProcessingRecord = new MailProcessingRecord();
        mailProcessingRecord.setMailId(MailProcessingRecord.getNextId());
        m_results.addNewMailRecord(mailProcessingRecord);
        mailProcessingRecord.setTimeConnectStart(System.currentTimeMillis());

        Message message = null;
        try {
            try {
                Session session = getMailSession();
                message = m_mailSender.createMail(session, mailProcessingRecord);
            } catch (Exception e) {
                mailProcessingRecord.setErrorTextSending("Could not send mail");
                throw e;
            }
            try {
                setMailFromAndTo(message, mailProcessingRecord);
            } catch (Exception e) {
                mailProcessingRecord.setErrorTextSending("Could not set recipient");
                throw e;
            }
            try {
                mailProcessingRecord.setTimeSendStart(System.currentTimeMillis());
                Transport.send(message);
                mailProcessingRecord.setTimeSendEnd(System.currentTimeMillis());
            } catch (MessagingException e) {
                mailProcessingRecord.setErrorTextSending("Could not be transported.");
                throw e;
            }
        } catch (Exception e) {
            throw new SamplingException("sample failed", e);
        }
    }

    private Session getMailSession() {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", m_host);
        props.put("mail.smtp.port", Integer.toString(m_port));
        return Session.getDefaultInstance(props, null);
    }
}
