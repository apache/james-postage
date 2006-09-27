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

import org.apache.james.postage.configuration.MailSender;
import org.apache.james.postage.result.MailProcessingRecord;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class DefaultMailFactory extends AbstractMailFactory implements MailFactory {

	protected void populateMessage(MimeMessage message, MailSender mailSender, MailProcessingRecord mailProcessingRecord) throws MessagingException {
        message.addHeader("Mime-Version", "1.0");
        message.addHeader("Content-Type", "multipart/mixed");

		Multipart multipart = new MimeMultipart("mixed");

		if (mailSender.sendTextPart()) {
		    int sizeMinText = mailSender.getSizeMinText();
		    int sizeMaxText = mailSender.getSizeMaxText();
		    MimeBodyPart part = new MimeBodyPart();

		    int mailSize = generateRandomPartSize(sizeMinText, sizeMaxText);
		    mailProcessingRecord.setByteSendText(mailSize);

		    StringBuffer textBody = new StringBuffer(mailSize);
		    for (int i = 0; i < mailSize; i++) textBody.append(getRandomChar());

		    part.setText(textBody.toString());

//                part.setDataHandler(new DataHandler(textBody.toString(), "text/plain"));
		    
		    multipart.addBodyPart(part);
		}

		if (mailSender.sendBinaryPart()) {
		    int sizeMinBinary = mailSender.getSizeMinBinary();
		    int sizeMaxBinary = mailSender.getSizeMaxBinary();
		    MimeBodyPart part = new MimeBodyPart();

		    int mailSize = generateRandomPartSize(sizeMinBinary, sizeMaxBinary);
		    mailProcessingRecord.setByteSendBinary(mailSize);

		    byte[] bytes = new byte[mailSize];
		    for (int i = 0; i < mailSize; i++) bytes[i] = getRandomByte();

		    part.setDataHandler(new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream")));
		    multipart.addBodyPart(part);
		}
		message.setContent(multipart);
	}
	
	protected Class getValidatorClass() {
		return DefaultMailValidator.class;
	}

}
