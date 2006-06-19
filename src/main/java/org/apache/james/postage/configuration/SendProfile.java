/***********************************************************************
 * Copyright (c) 2006 The Apache Software Foundation.                  *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/


package org.apache.james.postage.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class SendProfile {
    private String m_profileName;
    private boolean m_sourceInternal = true;
    private boolean m_targetInternal = true;

    private final List m_mailSenders = new ArrayList();

    public SendProfile(String profileName) {
        m_profileName = profileName;
    }

    public String getName() {
        return m_profileName;
    }

    public void setSourceInternal(boolean internal) {
        m_sourceInternal = internal;
    }

    public boolean isSourceInternal() {
        return m_sourceInternal;
    }

    public void setTargetInternal(boolean internal) {
        m_targetInternal = internal;
    }

    public boolean isTargetInternal() {
        return m_targetInternal;
    }

    public void addMailSender(MailSender mailSender) {
        m_mailSenders.add(mailSender);
    }

    public Iterator mailSenderIterator() {
        return m_mailSenders.iterator();
    }

    public int getTotalMailsPerMin() {
        Iterator iterator = m_mailSenders.iterator();
        int total = 0;
        while (iterator.hasNext()) {
            MailSender mailSender = (MailSender)iterator.next();
            total += mailSender.getSendPerMinute();
        }
        return total;
    }
}
