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
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.postage.result.MailProcessingRecord;

/**
 * this validator is used together with DefaultMailFactory
 */
public class DefaultMailValidator implements MailValidator {

    private static Log log = LogFactory.getLog(DefaultMailValidator.class);

    public boolean validate(Message message, MailProcessingRecord record) {

        MimeMultipart mimeMultipart;
        try {
            mimeMultipart = new MimeMultipart(message.getDataHandler().getDataSource());
        } catch (MessagingException e) {
            return false;
        }

        // figuring out the parts created by DefaultMailFactory
        int textPartSize = MailMatchingUtils.getMimePartSize(mimeMultipart, "text/plain");
        record.setByteReceivedText(textPartSize);
        int binaryPartSize = MailMatchingUtils.getMimePartSize(mimeMultipart, "application/octet-stream");
        record.setByteReceivedBinary(binaryPartSize);
        
        boolean textPartValid = textPartSize == record.getByteSendText();
        boolean binaryPartValid = binaryPartSize == record.getByteSendBinary();
        boolean valid = textPartValid && binaryPartValid;
        return valid;
    }

}
