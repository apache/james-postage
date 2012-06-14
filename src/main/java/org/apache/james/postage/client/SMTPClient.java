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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

import org.apache.james.postage.SamplingException;
import org.apache.james.postage.StartupException;
import org.apache.james.postage.configuration.MailSender;
import org.apache.james.postage.execution.Sampler;
import org.apache.james.postage.mail.HeaderConstants;
import org.apache.james.postage.result.MailProcessingRecord;
import org.apache.james.postage.result.PostageRunnerResult;
import org.apache.james.postage.user.UserList;

/**
 * connects as a SMTP client and handles all mail according to its configuration.<br/>
 * it is threadsafe and reentrant and thus can be reused over multiple parallel client session<br/>
 */
public class SMTPClient implements Sampler {

    private String host;
    private int port;
    private UserList internalUsers;
    private UserList externalUsers;
    private PostageRunnerResult results;
    private MailSender mailSender;

    public SMTPClient(String host, int port, UserList internalUsers, UserList externalUsers, PostageRunnerResult results, MailSender mailSender) {
        this.host = host;
        this.port = port;
        this.internalUsers = internalUsers;
        this.externalUsers = externalUsers;
        this.mailSender = mailSender;
        this.results = results;
    }

    public boolean checkAvailability() throws StartupException {
        try {

            MailProcessingRecord proformaMailProcessingRecord = new MailProcessingRecord();
            Session session = getMailSession();
            proformaMailProcessingRecord.setMailId(HeaderConstants.JAMES_POSTAGE_STARTUPCHECK_HEADER_ID);
            Message message = this.mailSender.createMail(session, proformaMailProcessingRecord);
            setMailFromAndTo(message, proformaMailProcessingRecord);
            Transport.send(message);
        } catch (Exception e) {
            throw new StartupException("Inbound SMTP service not available with " + this.toString() , e);
        }
        return true;
    }

    private void setMailFromAndTo(Message message, MailProcessingRecord mailProcessingRecord) throws MessagingException {

        String senderUsername;
        String senderMailAddress;
        if (this.mailSender.getParentProfile().isSourceInternal()) {
            senderUsername = this.internalUsers.getRandomUsername();
        } else {
            senderUsername = this.externalUsers.getRandomUsername();
        }
        if (this.mailSender.getParentProfile().isSourceInternal()) {
            senderMailAddress = this.internalUsers.getEmailAddress(senderUsername);
        } else {
            senderMailAddress = this.externalUsers.getEmailAddress(senderUsername);
        }
        mailProcessingRecord.setSender(senderUsername);
        mailProcessingRecord.setSenderMailAddress(senderMailAddress);
        message.setFrom(new InternetAddress(senderMailAddress));

        String recepientUsername;
        String recepientMailAddress;
        if (this.mailSender.getParentProfile().isTargetInternal()) {
            recepientUsername = this.internalUsers.getRandomUsername();
        } else {
            recepientUsername = this.externalUsers.getRandomUsername();
        }
        if (this.mailSender.getParentProfile().isTargetInternal()) {
            recepientMailAddress = this.internalUsers.getEmailAddress(recepientUsername);
        } else {
            recepientMailAddress = this.externalUsers.getEmailAddress(recepientUsername);
        }
        mailProcessingRecord.setReceiver(recepientUsername);
        mailProcessingRecord.setReceiverMailAddress(recepientMailAddress);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recepientMailAddress));
    }

    public synchronized void doSample() throws SamplingException {

        MailProcessingRecord mailProcessingRecord = new MailProcessingRecord();
        mailProcessingRecord.setMailId(MailProcessingRecord.getNextId());
        this.results.addNewMailRecord(mailProcessingRecord);
        mailProcessingRecord.setTimeConnectStart(System.currentTimeMillis());

        Message message = null;
        try {
            try {
                Session session = getMailSession();
                message = this.mailSender.createMail(session, mailProcessingRecord);
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
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.port", Integer.toString(this.port));
        return Session.getDefaultInstance(props, null);
    }

    @Override
    public String toString() {
        return "SMTPClient [host=" + host + ", port=" + port + "]";
    }

}
