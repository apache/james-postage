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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AvalonToPostageLogger implements org.apache.avalon.framework.logger.Logger {

    private static Log log = LogFactory.getLog(AvalonToPostageLogger.class);

    private boolean m_debugEnabled = true;

    public void debug(java.lang.String string) {
        log.debug(string);
    }

    public void debug(java.lang.String string, java.lang.Throwable throwable) {
        log.debug(string, throwable);
    }

    public boolean isDebugEnabled() {
        return m_debugEnabled;
    }

    public void disableDebug() {
        m_debugEnabled = false;
    }

    public void info(java.lang.String string) {
        log.debug(string);
    }

    public void info(java.lang.String string, java.lang.Throwable throwable) {
        log.debug(string, throwable);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void warn(java.lang.String string) {
        log.info(string);
    }

    public void warn(java.lang.String string, java.lang.Throwable throwable) {
        log.info(string, throwable);
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void error(java.lang.String string) {
        log.info(string);
    }

    public void error(java.lang.String string, java.lang.Throwable throwable) {
        log.info(string, throwable);
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public void fatalError(java.lang.String string) {
        log.error(string);
    }

    public void fatalError(java.lang.String string, java.lang.Throwable throwable) {
        log.error(string, throwable);
    }

    public boolean isFatalErrorEnabled() {
        return true;
    }

    public org.apache.avalon.framework.logger.Logger getChildLogger(java.lang.String string) {
        return this;
    }

}
