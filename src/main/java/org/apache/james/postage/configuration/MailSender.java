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

import org.apache.james.postage.mail.MailFactory;
import org.apache.james.postage.mail.DefaultMailFactory;
import org.apache.james.postage.result.MailProcessingRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Session;
import javax.mail.Message;

/**
 * specifies, how mail is to be generated and sent, as coming from the configuration (<send> element)<br/>
 * the source and target are specified by the parent SendProfile instance<br/>
 * <br/>
 * TODO init increaseSendPerMinute & maxSendPerMinute from config
 */
public class MailSender {

    private static Log log = LogFactory.getLog(MailSender.class);

    private int sendPerMinute = 1;
    private double increaseSendPerMinute = 0.0;
    private int maxSendPerMinute = -1;
    private String subject = "test";
    private int sizeMinText = 0;
    private int sizeMaxText = 1000;
    private int sizeMinBinary = -1;
    private int sizeMaxBinary = -1;
    private SendProfile m_parentProfile;

    private String m_mailFactoryClassname = null;
    private Class<? extends MailFactory> m_mailFactoryClass = null;

    public MailSender(SendProfile parent) {
        m_parentProfile = parent;
    }

    public int getSendPerMinute() {
        return sendPerMinute;
    }

    public void setSendPerMinute(int sendPerMinute) {
        this.sendPerMinute = sendPerMinute;
    }

    public double getIncreaseSendPerMinute() {
        return increaseSendPerMinute;
    }

    public void setIncreaseSendPerMinute(double increaseSendPerMinute) {
        this.increaseSendPerMinute = increaseSendPerMinute;
    }

    public double getMaxSendPerMinute() {
        return maxSendPerMinute;
    }

    public void setMaxSendPerMinute(int maxSendPerMinute) {
        this.maxSendPerMinute = maxSendPerMinute;
    }

    /**
     * how much emails are to be sent in the specified minute
     * (taking into account the increase and max values)
     * @param minute
     * @return mails to be sent
     */
    public int getSendPerMinute(int minute) {
        int increased = sendPerMinute + (int)(increaseSendPerMinute * minute);
        if (maxSendPerMinute > 0) {
            if (increased > maxSendPerMinute) increased = maxSendPerMinute;
        }
        return increased;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSizeMinText() {
        return sizeMinText;
    }

    public void setSizeMinText(int sizeMinText) {
        this.sizeMinText = sizeMinText;
    }

    public int getSizeMaxText() {
        return sizeMaxText;
    }

    public void setSizeMaxText(int sizeMaxText) {
        this.sizeMaxText = sizeMaxText;
    }

    public int getSizeMinBinary() {
        return sizeMinBinary;
    }

    public void setSizeMinBinary(int sizeMinBinary) {
        this.sizeMinBinary = sizeMinBinary;
    }

    public int getSizeMaxBinary() {
        return sizeMaxBinary;
    }

    public void setSizeMaxBinary(int sizeMaxBinary) {
        this.sizeMaxBinary = sizeMaxBinary;
    }

    public SendProfile getParentProfile() {
        return m_parentProfile;
    }

    public String getMailFactoryClassname() {
        return m_mailFactoryClassname;
    }

    public void setMailFactoryClassname(String mailFactoryClassname) {
        this.m_mailFactoryClassname = mailFactoryClassname;
    }

    public boolean sendTextPart() {
        return getSizeMinText() >= 0 && getSizeMaxText() >= 1;
    }

    public boolean sendBinaryPart() {
        return getSizeMinBinary() >= 0 && getSizeMaxBinary() >= 1;
    }

    public Message createMail(Session mailSession, MailProcessingRecord mailProcessingRecord) {
        return getMailFactory().createMail(mailSession, this, mailProcessingRecord);
        // TODO assert, that created mail conforms to some rules, e.g. has Postage X-headers set
    }

    public MailFactory getMailFactory() {
        MailFactory mailFactory = null;

        // class is configured, but not yet loaded
        if (m_mailFactoryClassname != null && m_mailFactoryClass == null) {
            try {
                m_mailFactoryClass = (Class<? extends MailFactory>) Class.forName(m_mailFactoryClassname);
            } catch (ClassNotFoundException e) {
                log.error("failed to load MailFactory class " + m_mailFactoryClassname, e);
            }
        }

        // create instance, if custom class is given
        if (m_mailFactoryClass != null) {
            try {
                mailFactory = m_mailFactoryClass.newInstance();
            } catch (Exception e) {
                log.error("failed to create instance if MailFactory class " + m_mailFactoryClassname, e);
            }
        }

        if (mailFactory == null) mailFactory = new DefaultMailFactory();
        return mailFactory;
    }


}
