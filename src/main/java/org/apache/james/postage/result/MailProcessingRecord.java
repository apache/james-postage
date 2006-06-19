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


package org.apache.james.postage.result;

/**
 * contains all gathered data concerning one mail message
 */
public class MailProcessingRecord {
    
    private static String SEPARATOR = ",";
    private static int m_messageId = 1;

    boolean matchedSentAndReceived = false;

    long timeConnectStart;
    String mailId;
    String subject;
    String sender;
    String senderMailAddress;
    String receiver;
    String receiverMailAddress;
    long timeSendStart;
    long timeSendEnd;
    long byteSendText;
    long byteSendBinary;
    int errorNumberSending;
    String errorTextSending;

    long timeReceived;
    long timeServerReceived;
    long timeFetchStart;
    long timeFetchEnd;
    long byteReceivedText;
    long byteReceivedBinary;
    long byteReceivedTotal;
    String receivingQueue;

    public synchronized static String getNextId() {
        return "" + (m_messageId++);
    }

    public boolean isMatchedSentAndReceived() {
        return matchedSentAndReceived;
    }

    public long getTimeConnectStart() {
        return timeConnectStart;
    }

    public void setTimeConnectStart(long timeConnectStart) {
        this.timeConnectStart = timeConnectStart;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderMailAddress() {
        return senderMailAddress;
    }

    public void setSenderMailAddress(String senderMailAddress) {
        this.senderMailAddress = senderMailAddress;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMailAddress() {
        return receiverMailAddress;
    }

    public void setReceiverMailAddress(String receiverMailAddress) {
        this.receiverMailAddress = receiverMailAddress;
    }

    public long getTimeSendStart() {
        return timeSendStart;
    }

    public void setTimeSendStart(long timeSendStart) {
        this.timeSendStart = timeSendStart;
    }

    public long getTimeSendEnd() {
        return timeSendEnd;
    }

    public void setTimeSendEnd(long timeSendEnd) {
        this.timeSendEnd = timeSendEnd;
    }

    public long getByteSendText() {
        return byteSendText;
    }

    public void setByteSendText(long byteSendText) {
        this.byteSendText = byteSendText;
    }

    public long getByteSendBinary() {
        return byteSendBinary;
    }

    public void setByteSendBinary(long byteSendBinary) {
        this.byteSendBinary = byteSendBinary;
    }

    public long getByteReceivedText() {
        return byteReceivedText;
    }

    public void setByteReceivedText(long byteReceivedText) {
        this.byteReceivedText = byteReceivedText;
    }

    public long getByteReceivedBinary() {
        return byteReceivedBinary;
    }

    public void setByteReceivedBinary(long byteReceivedBinary) {
        this.byteReceivedBinary = byteReceivedBinary;
    }

    public long getByteReceivedTotal() {
        return byteReceivedTotal;
    }

    public void setByteReceivedTotal(long byteReceivedTotal) {
        this.byteReceivedTotal = byteReceivedTotal;
    }

    public int getErrorNumberSending() {
        return errorNumberSending;
    }

    public void setErrorNumberSending(int errorNumberSending) {
        this.errorNumberSending = errorNumberSending;
    }

    public String getErrorTextSending() {
        return errorTextSending;
    }

    public void setErrorTextSending(String errorTextSending) {
        this.errorTextSending = errorTextSending;
    }

    public long getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(long timeReceived) {
        this.timeReceived = timeReceived;
    }

    public long getTimeFetchStart() {
        return timeFetchStart;
    }

    public void setTimeFetchStart(long timeFetchStart) {
        this.timeFetchStart = timeFetchStart;
    }

    public long getTimeFetchEnd() {
        return timeFetchEnd;
    }

    public void setTimeFetchEnd(long timeFetchEnd) {
        this.timeFetchEnd = timeFetchEnd;
    }

    public long getTimeServerReceived() {
        return timeServerReceived;
    }

    public void setTimeServerReceived(long timeServerReceived) {
        this.timeServerReceived = timeServerReceived;
    }

    public String getReceivingQueue() {
        return receivingQueue;
    }

    public void setReceivingQueue(String receivingQueue) {
        this.receivingQueue = receivingQueue;
    }

    public void merge(MailProcessingRecord anotherRecord) {
        if (matchedSentAndReceived) throw new IllegalStateException("already merged");
        matchedSentAndReceived = true;
        
        if (timeConnectStart == 0) timeConnectStart = anotherRecord.timeConnectStart;
        if (mailId == null) mailId = anotherRecord.mailId;
        if (subject == null) subject = anotherRecord.subject;
        if (sender == null) sender = anotherRecord.sender;
        if (senderMailAddress == null) senderMailAddress = anotherRecord.senderMailAddress;
        if (receiver == null) receiver = anotherRecord.receiver;
        if (receiverMailAddress == null) receiverMailAddress = anotherRecord.receiverMailAddress;
        if (timeSendStart == 0) timeSendStart = anotherRecord.timeSendStart;
        if (timeSendEnd == 0) timeSendEnd = anotherRecord.timeSendEnd;
        if (byteSendText == 0) byteSendText = anotherRecord.byteSendText;
        if (byteSendBinary == 0) byteSendBinary = anotherRecord.byteSendBinary;
        if (byteReceivedText == 0) byteReceivedText = anotherRecord.byteReceivedText;
        if (byteReceivedBinary == 0) byteReceivedBinary = anotherRecord.byteReceivedBinary;
        if (byteReceivedTotal == 0) byteReceivedTotal = anotherRecord.byteReceivedTotal;
        if (errorNumberSending == 0) errorNumberSending = anotherRecord.errorNumberSending;
        if (errorTextSending == null) errorTextSending = anotherRecord.errorTextSending;
        if (timeReceived == 0) timeReceived = anotherRecord.timeReceived;
        if (timeFetchStart == 0) timeFetchStart = anotherRecord.timeFetchStart;
        if (timeFetchEnd == 0) timeFetchEnd = anotherRecord.timeFetchEnd;
        if (timeServerReceived == 0) timeServerReceived = anotherRecord.timeServerReceived;
        if (receivingQueue == null) receivingQueue = anotherRecord.receivingQueue;
    }

    public static StringBuffer writeHeader() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("timeConnectStart").append(SEPARATOR);
        stringBuffer.append("mailId").append(SEPARATOR);
        stringBuffer.append("ReceivedMatchedSent").append(SEPARATOR);
        stringBuffer.append("subject").append(SEPARATOR);
        stringBuffer.append("sender").append(SEPARATOR);
        stringBuffer.append("senderMailAddress").append(SEPARATOR);
        stringBuffer.append("receiver").append(SEPARATOR);
        stringBuffer.append("receiverMailAddress").append(SEPARATOR);
        stringBuffer.append("timeSendStart").append(SEPARATOR);
        stringBuffer.append("timeSendEnd").append(SEPARATOR);
        stringBuffer.append("byteSendText").append(SEPARATOR);
        stringBuffer.append("byteSendBinary").append(SEPARATOR);
        stringBuffer.append("byteReceivedText").append(SEPARATOR);
        stringBuffer.append("byteReceivedBinary").append(SEPARATOR);
        stringBuffer.append("byteReceivedTotal").append(SEPARATOR);
        stringBuffer.append("errorNumberSending").append(SEPARATOR);
        stringBuffer.append("errorTextSending").append(SEPARATOR);
        stringBuffer.append("timeReceived").append(SEPARATOR);
        stringBuffer.append("timeFetchStart").append(SEPARATOR);
        stringBuffer.append("timeFetchEnd").append(SEPARATOR);
        stringBuffer.append("timeServerReceived").append(SEPARATOR);
        stringBuffer.append("receivingQueue").append(SEPARATOR);
        stringBuffer.append("\r\n");

        return stringBuffer;
    }

    public StringBuffer writeData() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(timeConnectStart).append(SEPARATOR);
        stringBuffer.append(mailId).append(SEPARATOR);
        stringBuffer.append(isMatchedSentAndReceived() ? "MATCHED" : "UNMATCHED").append(SEPARATOR);
        stringBuffer.append(subject).append(SEPARATOR);
        stringBuffer.append(sender).append(SEPARATOR);
        stringBuffer.append(senderMailAddress).append(SEPARATOR);
        stringBuffer.append(receiver).append(SEPARATOR);
        stringBuffer.append(receiverMailAddress).append(SEPARATOR);
        stringBuffer.append(timeSendStart).append(SEPARATOR);
        stringBuffer.append(timeSendEnd).append(SEPARATOR);
        stringBuffer.append(byteSendText).append(SEPARATOR);
        stringBuffer.append(byteSendBinary).append(SEPARATOR);
        stringBuffer.append(byteReceivedText).append(SEPARATOR);
        stringBuffer.append(byteReceivedBinary).append(SEPARATOR);
        stringBuffer.append(byteReceivedTotal).append(SEPARATOR);
        stringBuffer.append(errorNumberSending).append(SEPARATOR);
        stringBuffer.append(errorTextSending).append(SEPARATOR);
        stringBuffer.append(timeReceived).append(SEPARATOR);
        stringBuffer.append(timeFetchStart).append(SEPARATOR);
        stringBuffer.append(timeFetchEnd).append(SEPARATOR);
        stringBuffer.append(timeServerReceived).append(SEPARATOR);
        stringBuffer.append(receivingQueue).append(SEPARATOR);
        stringBuffer.append("\r\n");

        return stringBuffer;
    }

}
