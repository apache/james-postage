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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * specifies for the contained MailSenders, what are source and target of each mail they generate
 */
public class SendProfile {
    private String profileName;
    private boolean sourceInternal = true;
    private boolean targetInternal = true;

    private final List<MailSender> mailSenders = new ArrayList<MailSender>();

    public SendProfile(String profileName) {
        this.profileName = profileName;
    }

    public String getName() {
        return this.profileName;
    }

    public void setSourceInternal(boolean internal) {
        this.sourceInternal = internal;
    }

    public boolean isSourceInternal() {
        return this.sourceInternal;
    }

    public void setTargetInternal(boolean internal) {
        this.targetInternal = internal;
    }

    public boolean isTargetInternal() {
        return this.targetInternal;
    }

    public void addMailSender(MailSender mailSender) {
        this.mailSenders.add(mailSender);
    }

    public Iterator<MailSender> mailSenderIterator() {
        return this.mailSenders.iterator();
    }

    public int getTotalMailsPerMin() {
        Iterator<MailSender> iterator = this.mailSenders.iterator();
        int total = 0;
        while (iterator.hasNext()) {
            MailSender mailSender = iterator.next();
            total += mailSender.getSendPerMinute();
        }
        return total;
    }
}
