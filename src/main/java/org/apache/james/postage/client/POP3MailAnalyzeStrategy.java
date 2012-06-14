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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.james.postage.mail.MailAnalyzeStrategy;
import org.apache.james.postage.result.PostageRunnerResult;

public class POP3MailAnalyzeStrategy extends MailAnalyzeStrategy {

    private int mailNumber;
    private int mailIndex;
    private org.apache.commons.net.pop3.POP3Client pop3Client;

    public POP3MailAnalyzeStrategy(String receivingQueueName, PostageRunnerResult results, 
                                   org.apache.commons.net.pop3.POP3Client pop3Client, 
                                   int mailNumber, int mailIndex) {
        super(receivingQueueName, results);
        this.pop3Client = pop3Client;
        this.mailNumber = mailNumber;
        this.mailIndex = mailIndex;
    }

    protected MimeMessage loadMessage() throws Exception {
        Reader reader = pop3Client.retrieveMessage(mailNumber);
        BufferedReader mailReader = new BufferedReader(reader);
        InputStream in = new ReaderInputStream(mailReader);
        MimeMessage message;
        try {
            message = new MimeMessage(null, in);
            in.close();
        } catch (IOException e) {
            log.info("failed to close POP3 mail reader.");
            throw e;
        } catch (MessagingException e) {
            log.info("failed to process POP3 mail. remains on server");
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("error closing mail input stream");
                }
            }
            if (mailReader != null) {
                try {
                    mailReader.close();
                } catch (IOException e) {
                    log.warn("error closing mail reader");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn("error closing (mail) reader");
                }
            }
        }

        return message;
    }
    
    protected void dismissMessage() throws Exception {
        try {
            pop3Client.deleteMessage(mailIndex + 1); // don't retrieve again next time
        } catch (Exception e) {
            log.info("failed to delete POP3 mail.");
            throw e;
        }
    }


}
