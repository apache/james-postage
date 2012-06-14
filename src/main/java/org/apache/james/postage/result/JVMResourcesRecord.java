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

    private final long timestamp = System.currentTimeMillis();
    private String errorMessage = null;

    private long memoryCommitted = -1;
    private long memoryInit = -1;
    private long memoryMax = -1;
    private long memoryUsed = -1;
    private long threadCountPeak = -1;
    private long threadCountCurrent = -1;
    private long threadCountTotalStarted = -1;

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getMemoryCommitted() {
        return this.memoryCommitted;
    }

    public void setMemoryCommitted(long memoryCommitted) {
        this.memoryCommitted = memoryCommitted;
    }

    public long getMemoryInit() {
        return this.memoryInit;
    }

    public void setMemoryInit(long memoryInit) {
        this.memoryInit = memoryInit;
    }

    public long getMemoryMax() {
        return this.memoryMax;
    }

    public void setMemoryMax(long memoryMax) {
        this.memoryMax = memoryMax;
    }

    public long getMemoryUsed() {
        return this.memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public long getThreadCountPeak() {
        return this.threadCountPeak;
    }

    public void setThreadCountPeak(long threadCountPeak) {
        this.threadCountPeak = threadCountPeak;
    }

    public long getThreadCountCurrent() {
        return this.threadCountCurrent;
    }

    public void setThreadCountCurrent(long threadCountCurrent) {
        this.threadCountCurrent = threadCountCurrent;
    }

    public long getThreadCountTotalStarted() {
        return this.threadCountTotalStarted;
    }

    public void setThreadCountTotalStarted(long threadCountTotalStarted) {
        this.threadCountTotalStarted = threadCountTotalStarted;
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
        stringBuffer.append(this.timestamp).append(SEPARATOR);
        stringBuffer.append(this.errorMessage).append(SEPARATOR);
        stringBuffer.append(this.memoryInit).append(SEPARATOR);
        stringBuffer.append(this.memoryMax).append(SEPARATOR);
        stringBuffer.append(this.memoryCommitted).append(SEPARATOR);
        stringBuffer.append(this.memoryUsed).append(SEPARATOR);
        stringBuffer.append(this.threadCountPeak).append(SEPARATOR);
        stringBuffer.append(this.threadCountCurrent).append(SEPARATOR);
        stringBuffer.append(this.threadCountTotalStarted).append(SEPARATOR);
        stringBuffer.append("\r\n");

        return stringBuffer;
    }

}
