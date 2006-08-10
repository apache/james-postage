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


package org.apache.james.postage.result;

/**
 * records data of fundamental resource consumption of James' JVM
 */
public class JVMResourcesRecord {
    private static String SEPARATOR = ",";

    private final long m_timestamp = System.currentTimeMillis();
    private String m_errorMessage = null;

    private long m_memoryCommitted = -1;
    private long m_memoryInit = -1;
    private long m_memoryMax = -1;
    private long m_memoryUsed = -1;
    private long m_threadCountPeak = -1;
    private long m_threadCountCurrent = -1;
    private long m_threadCountTotalStarted = -1;

    public String getErrorMessage() {
        return m_errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        m_errorMessage = errorMessage;
    }

    public long getMemoryCommitted() {
        return m_memoryCommitted;
    }

    public void setMemoryCommitted(long memoryCommitted) {
        this.m_memoryCommitted = memoryCommitted;
    }

    public long getMemoryInit() {
        return m_memoryInit;
    }

    public void setMemoryInit(long memoryInit) {
        this.m_memoryInit = memoryInit;
    }

    public long getMemoryMax() {
        return m_memoryMax;
    }

    public void setMemoryMax(long memoryMax) {
        this.m_memoryMax = memoryMax;
    }

    public long getMemoryUsed() {
        return m_memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.m_memoryUsed = memoryUsed;
    }

    public long getThreadCountPeak() {
        return m_threadCountPeak;
    }

    public void setThreadCountPeak(long threadCountPeak) {
        this.m_threadCountPeak = threadCountPeak;
    }

    public long getThreadCountCurrent() {
        return m_threadCountCurrent;
    }

    public void setThreadCountCurrent(long threadCountCurrent) {
        this.m_threadCountCurrent = threadCountCurrent;
    }

    public long getThreadCountTotalStarted() {
        return m_threadCountTotalStarted;
    }

    public void setThreadCountTotalStarted(long threadCountTotalStarted) {
        this.m_threadCountTotalStarted = threadCountTotalStarted;
    }

    public static StringBuffer writeHeader() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("timestamp").append(SEPARATOR);
        stringBuffer.append("errorMessage").append(SEPARATOR);
        stringBuffer.append("memoryMin").append(SEPARATOR);
        stringBuffer.append("memoryMax").append(SEPARATOR);
        stringBuffer.append("memoryCommitted").append(SEPARATOR);
        stringBuffer.append("memoryUsed").append(SEPARATOR);
        stringBuffer.append("threadCountPeak").append(SEPARATOR);
        stringBuffer.append("threadCountCurrent").append(SEPARATOR);
        stringBuffer.append("threadCountTotalStarted").append(SEPARATOR);
        stringBuffer.append("\r\n");

        return stringBuffer;
    }

    public StringBuffer writeData() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(m_timestamp).append(SEPARATOR);
        stringBuffer.append(m_errorMessage).append(SEPARATOR);
        stringBuffer.append(m_memoryInit).append(SEPARATOR);
        stringBuffer.append(m_memoryMax).append(SEPARATOR);
        stringBuffer.append(m_memoryCommitted).append(SEPARATOR);
        stringBuffer.append(m_memoryUsed).append(SEPARATOR);
        stringBuffer.append(m_threadCountPeak).append(SEPARATOR);
        stringBuffer.append(m_threadCountCurrent).append(SEPARATOR);
        stringBuffer.append(m_threadCountTotalStarted).append(SEPARATOR);
        stringBuffer.append("\r\n");

        return stringBuffer;
    }

}
