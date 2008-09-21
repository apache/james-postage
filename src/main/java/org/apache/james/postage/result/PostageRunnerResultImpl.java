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

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class PostageRunnerResultImpl implements PostageRunnerResult {

    private static Log log = LogFactory.getLog(PostageRunnerResultImpl.class);

    private Map<String, MailProcessingRecord> m_matchedMailResults = initMatchedMailResultContainer();

    private final Map<String, MailProcessingRecord> m_unmatchedMailResults = new HashedMap();

    private List<ErrorRecord> m_errors = initErrorResultContainer();

    private List<JVMResourcesRecord> m_jvmStatistics = initMatchedJVMStatisticsResultContainer();

    private long m_TimestampFirstResult = -1;

    private long m_TimestampLastResult = -1;

    private long m_matchedMailCounter = 0;

    private long m_validMailCounter = 0;

    private Map<String, String>  m_environmentInfo = new LinkedHashMap<String, String>();

    public void addNewMailRecord(MailProcessingRecord mailProcessingRecord) {

        if (m_TimestampFirstResult <= 0) m_TimestampFirstResult = System.currentTimeMillis();
        m_TimestampLastResult = System.currentTimeMillis();

        MailProcessingRecord prevMailProcessingRecord = m_unmatchedMailResults.put(mailProcessingRecord.getMailId(), mailProcessingRecord);
        if (prevMailProcessingRecord != null) {
            log.error("mail result already contained in unmatched list!");
        }
    }

    public synchronized MailProcessingRecord matchMailRecord(MailProcessingRecord mailProcessingRecord) {
        if (mailProcessingRecord == null) return null;
        String mailId = mailProcessingRecord.getMailId();
        if (mailId == null) return null;

        if (m_unmatchedMailResults.containsKey(mailId)) {
            // merge both mail result objects into one and move it to matched list
            MailProcessingRecord match = m_unmatchedMailResults.remove(mailId);
            log.info("matched test mail having id = " + mailId + " received by queue = " + mailProcessingRecord.getReceivingQueue());

            match.merge(mailProcessingRecord); // copy new data to saved record

            m_matchedMailResults.put(mailId, match);
            m_matchedMailCounter++;
            return match;
        } else if (m_matchedMailResults.containsKey(mailId)) {
            log.warn("mail already matched for mailId = " + mailId);
        } else {
            log.warn("mail match candidate has unknown (purged?) mailId = " + mailId);
        }

        return null;
    }
    
    public void recordValidatedMatch(MailProcessingRecord matchedAndMergedRecord) {
        if (!m_matchedMailResults.values().contains(matchedAndMergedRecord)) {
            log.error("cannot record validation result for (already written?) result having id " 
                       + matchedAndMergedRecord.getMailId());
            return;
        }
        
        if (matchedAndMergedRecord.isReceivedValid()) m_validMailCounter++;
    }

    public void addJVMResult(JVMResourcesRecord jvmResourcesRecord) {
        m_jvmStatistics.add(jvmResourcesRecord);
    }

    public void setEnvironmentDescription(Map<String, String> descriptionItems) {
        m_environmentInfo.putAll(descriptionItems);
    }

    public long getUnmatchedMails() {
        return m_unmatchedMailResults.size();
    }

    public long getMatchedMails() {
        return m_matchedMailCounter;
    }

    public long getValidMails() {
        return m_validMailCounter;
    }

    public void writeMailResults(OutputStreamWriter outputStreamWriter, boolean flushOnlyMatched) throws IOException {
        writeMatchedMailResults(outputStreamWriter);
        if (!flushOnlyMatched) {
            writeUnmatchedMailResults(outputStreamWriter);
            writeGeneralData(outputStreamWriter);
        }
    }

    private void writeUnmatchedMailResults(OutputStreamWriter outputStreamWriter) throws IOException {
        writeMailResults(m_unmatchedMailResults, outputStreamWriter);
        outputStreamWriter.flush();
    }

    private void writeMatchedMailResults(OutputStreamWriter outputStreamWriter) throws IOException {
        Map<String, MailProcessingRecord> writeResults = m_matchedMailResults; // keep current results for writing
        m_matchedMailResults = initMatchedMailResultContainer(); // establish new map for further unwritten results
        writeMailResults(writeResults, outputStreamWriter);
        outputStreamWriter.flush();
    }

    private void writeGeneralData(OutputStreamWriter outputStreamWriter) throws IOException {
        outputStreamWriter.write("start," + m_TimestampFirstResult + "," + new Date(m_TimestampFirstResult) + "\r\n");
        outputStreamWriter.write("end," + m_TimestampLastResult + "," + new Date(m_TimestampLastResult) + "\r\n");
        outputStreamWriter.write("current," + System.currentTimeMillis() + "," + new Date() + "\r\n");

        Iterator<String> iterator = m_environmentInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String elementName = iterator.next();
            String elementValue = m_environmentInfo.get(elementName);
            outputStreamWriter.write(elementName + "," + elementValue + "\r\n");
        }
    }

    public long getTimestampFirstResult() {
        return m_TimestampFirstResult;
    }

    public long getTimestampLastResult() {
        return m_TimestampLastResult;
    }

    public void addError(int errorNumber, String errorMessage) {
        m_errors.add(new ErrorRecord(errorNumber, errorMessage));
    }

    public long getErrorCount() {
        return m_errors.size();
    }

    private void writeMailResults(Map<String, MailProcessingRecord> mailResults, OutputStreamWriter outputStreamWriter) throws IOException {
        Iterator<MailProcessingRecord> iterator = mailResults.values().iterator();
        while (iterator.hasNext()) {
            MailProcessingRecord record = iterator.next();
            String resultString = record.writeData().toString();
            outputStreamWriter.write(resultString);
        }
    }

    private Map<String, MailProcessingRecord> initMatchedMailResultContainer() {
        return new HashedMap();
    }

    private List<JVMResourcesRecord> initMatchedJVMStatisticsResultContainer() {
        return new ArrayList<JVMResourcesRecord>();
    }

    private List<ErrorRecord> initErrorResultContainer() {
        return new ArrayList<ErrorRecord>();
    }

    public void writeResults(String filenameMailResults, String filenameJVMStatistics, String filenameErrors, boolean flushMatchedMailOnly) {
        if (filenameMailResults != null) writeMailResults(filenameMailResults, flushMatchedMailOnly);
        if (filenameJVMStatistics != null) writeJVMStatistics(filenameJVMStatistics);
        if (filenameErrors != null) writeErrors(filenameErrors);
    }

    public void writeMailResults(String filenameMailResults, boolean flushMatchedMailOnly) {
       FileOutputStream outputStream = null;
       OutputStreamWriter outputStreamWriter = null;
       try {
           outputStream = new FileOutputStream(filenameMailResults, true);
           outputStreamWriter = new OutputStreamWriter(outputStream);
           if (new File(filenameMailResults).length() <= 0) outputStreamWriter.write(MailProcessingRecord.writeHeader().toString());
           writeMailResults(outputStreamWriter, flushMatchedMailOnly);
       } catch (IOException e) {
           log.error("error writing mail results to file " + filenameMailResults, e);
       } finally {
           try {
               if (outputStreamWriter != null) outputStreamWriter.close();
               if (outputStream != null) outputStream.close();
               log.info("postage mail results completely written to file " + filenameMailResults);
           } catch (IOException e) {
               log.error("error closing stream", e);
           }
       }
    }

    public void writeJVMStatistics(String filenameJVMStatistics) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStream = new FileOutputStream(filenameJVMStatistics, true);
            outputStreamWriter = new OutputStreamWriter(outputStream);
            if (new File(filenameJVMStatistics).length() <= 0) outputStreamWriter.write(JVMResourcesRecord.writeHeader().toString());
            writeJVMStatisticsResults(outputStreamWriter);
        } catch (IOException e) {
            log.error("error writing JVM statistic results to file " + filenameJVMStatistics, e);
        } finally {
            try {
                if (outputStreamWriter != null) outputStreamWriter.close();
                if (outputStream != null) outputStream.close();
                log.info("postage JVM statistic results completely written to file " + filenameJVMStatistics);
            } catch (IOException e) {
                log.error("error closing stream", e);
            }
        }
    }

    private void writeJVMStatisticsResults(OutputStreamWriter outputStreamWriter) throws IOException {
        List<JVMResourcesRecord> unwrittenResults = m_jvmStatistics;
        m_jvmStatistics = initMatchedJVMStatisticsResultContainer();
        Iterator<JVMResourcesRecord> iterator = unwrittenResults.iterator();
        while (iterator.hasNext()) {
            JVMResourcesRecord record = iterator.next();
            String resultString = record.writeData().toString();
            outputStreamWriter.write(resultString);
        }
    }

    public void writeErrors(String filenameErrors) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStream = new FileOutputStream(filenameErrors, true);
            outputStreamWriter = new OutputStreamWriter(outputStream);

            if (new File(filenameErrors).length() <= 0) {
                outputStreamWriter.write("timestamp,number,message\r\n");
            }

            List<ErrorRecord> unwrittenResults = m_errors;
            m_errors = initErrorResultContainer();
            Iterator<ErrorRecord> iterator = unwrittenResults.iterator();
            while (iterator.hasNext()) {
                ErrorRecord record = iterator.next();

                StringBuffer resultString = new StringBuffer();
                resultString.append(record.m_timestamp).append(",");
                resultString.append(record.m_number).append(",");
                resultString.append(record.m_message).append(",");
                resultString.append("\r\n");

                outputStreamWriter.write(resultString.toString());
            }
        } catch (IOException e) {
            log.error("error writing JVM statistic results to file " + filenameErrors, e);
        } finally {
            try {
                if (outputStreamWriter != null) outputStreamWriter.close();
                if (outputStream != null) outputStream.close();
                log.info("postage JVM statistic results completely written to file " + filenameErrors);
            } catch (IOException e) {
                log.error("error closing stream", e);
            }
        }
    }

}

class ErrorRecord {
    long m_timestamp = -1;
    int m_number = -1;
    String m_message = null;

    public ErrorRecord(int m_number, String m_message) {
        this.m_timestamp = System.currentTimeMillis();
        this.m_number = m_number;
        this.m_message = m_message;
    }
}
