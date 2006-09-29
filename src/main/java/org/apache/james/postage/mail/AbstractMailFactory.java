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
package org.apache.james.postage.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.postage.configuration.MailSender;
import org.apache.james.postage.result.MailProcessingRecord;

/**
 * prototype of a mail factory, only missing the use case specific data. <br/>
 * it prepares the message with all Postage specific headers.<br/>
 * @see DefaultMailFactory as a template implementation  
 */
public abstract class AbstractMailFactory {

    private static Log log = LogFactory.getLog(DefaultMailFactory.class);

    private static final char[] CHARSET = new char[]
                                    {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                                     'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                                     'u', 'v', 'w', 'x', 'y', 'z',
                                     'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                     'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                                     'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static char getRandomChar() {
        return CHARSET[getRandomInt()];
    }

    private static int getRandomInt() {
        return (int)(Math.random() * (double)(CHARSET.length - 1));
    }

    public static byte getRandomByte() {
        return (byte)CHARSET[getRandomInt()];
    }

    public AbstractMailFactory() {
        super();
    }

    /**
     * generates a mail containing data common to all test mails: postage headers, 
     */
    public Message createMail(Session mailSession, MailSender mailSender, MailProcessingRecord mailProcessingRecord) {
        
        MimeMessage message = new MimeMessage(mailSession);
    
        try {
            message.addHeader(HeaderConstants.JAMES_POSTAGE_HEADER, "This is a test mail sent by James Postage");
            message.addHeader(HeaderConstants.JAMES_POSTAGE_VALIDATORCLASSNAME_HEADER, getValidatorClass().getName());
            message.setSubject(mailSender.getSubject());
            message.addHeader("Message-ID", "Postage-" + System.currentTimeMillis());
            mailProcessingRecord.setSubject(mailSender.getSubject());
    
            if (mailProcessingRecord.getMailId() != null) {
                message.addHeader(HeaderConstants.MAIL_ID_HEADER, mailProcessingRecord.getMailId());
            } else {
                log.warn("ID header is NULL!");
                throw new RuntimeException("could not create mail with ID = NULL");
            }
    
            populateMessage(message, mailSender, mailProcessingRecord);
    
        } catch (MessagingException e) {
            mailProcessingRecord.setErrorTextSending(e.toString());
            log.error("mail could not be created", e);
            return null;
        }
        return message;
    }

    /**
     * here, the test case specific data must be added to the message.
     * @param message
     * @param mailSender
     * @param mailProcessingRecord
     * @throws MessagingException
     */
    abstract protected void populateMessage(MimeMessage message, MailSender mailSender, MailProcessingRecord mailProcessingRecord) throws MessagingException;

    /**
     * the class representing the validator 
     * 
     * @return validator class
     */
    abstract protected Class getValidatorClass();
    
    protected int generateRandomPartSize(int sizeMin, int sizeMax) {
        return (int)(Math.random() * (double)(sizeMax - sizeMin)) + sizeMin;
    }

}
