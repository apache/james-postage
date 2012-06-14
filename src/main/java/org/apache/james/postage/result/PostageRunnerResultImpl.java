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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PostageRunnerResultImpl implements PostageRunnerResult {

    private static Log log = LogFactory.getLog(PostageRunnerResultImpl.class);

    private Map<String, MailProcessingRecord> matchedMailResults = initMatchedMailResultContainer();

    private final Map<String, MailProcessingRecord> unmatchedMailResults = new HashMap<String, MailProcessingRecord>();

    private List<ErrorRecord> errors = initErrorResultContainer();

    private List<JVMResourcesRecord> jvmStatistics = initMatchedJVMStatisticsResultContainer();

    private long TimestampFirstResult = -1;

    private long TimestampLastResult = -1;

    private long matchedMailCounter = 0;

    private long validMailCounter = 0;

    private Map<String, String> environmentInfo = new LinkedHashMap<String, String>();

    public void addNewMailRecord(MailProcessingRecord mailProcessingRecord) {

        if (this.TimestampFirstResult <= 0) this.TimestampFirstResult = System.currentTimeMillis();
        this.TimestampLastResult = System.currentTimeMillis();

        MailProcessingRecord prevMailProcessingRecord = this.unmatchedMailResults.put(mailProcessingRecord.getMailId(), mailProcessingRecord);
        if (prevMailProcessingRecord != null) {
            log.error("mail result already contained in unmatched list!");
        }
    }

    public synchronized MailProcessingRecord matchMailRecord(MailProcessingRecord mailProcessingRecord) {
        if (mailProcessingRecord == null) return null;
        String mailId = mailProcessingRecord.getMailId();
        if (mailId == null) return null;

        if (this.unmatchedMailResults.containsKey(mailId)) {
            // merge both mail result objects into one and move it to matched list
            MailProcessingRecord match = this.unmatchedMailResults.remove(mailId);
            log.info("matched test mail having id = " + mailId + " received by queue = " + mailProcessingRecord.getReceivingQueue());

            match.merge(mailProcessingRecord); // copy new data to saved record

            this.matchedMailResults.put(mailId, match);
            this.matchedMailCounter++;
            return match;
        } else if (this.matchedMailResults.containsKey(mailId)) {
            log.warn("mail already matched for mailId = " + mailId);
        } else {
            log.warn("mail match candidate has unknown (purged?) mailId = " + mailId);
        }

        return null;
    }
    
    public void recordValidatedMatch(MailProcessingRecord matchedAndMergedRecord) {
        if (!this.matchedMailResults.values().contains(matchedAndMergedRecord)) {
            log.error("cannot record validation result for (already written?) result having id " 
                       + matchedAndMergedRecord.getMailId());
            return;
        }
        
        if (matchedAndMergedRecord.isReceivedValid()) this.validMailCounter++;
    }

    public void addJVMResult(JVMResourcesRecord jvmResourcesRecord) {
        this.jvmStatistics.add(jvmResourcesRecord);
    }

    public void setEnvironmentDescription(Map<String, String> descriptionItems) {
        this.environmentInfo.putAll(descriptionItems);
    }

    public long getUnmatchedMails() {
        return this.unmatchedMailResults.size();
    }

    public long getMatchedMails() {
        return this.matchedMailCounter;
    }

    public long getValidMails() {
        return this.validMailCounter;
    }

    public void writeMailResults(OutputStreamWriter outputStreamWriter, boolean flushOnlyMatched) throws IOException {
        writeMatchedMailResults(outputStreamWriter);
        if (!flushOnlyMatched) {
            writeUnmatchedMailResults(outputStreamWriter);
            writeGeneralData(outputStreamWriter);
        }
    }

    private void writeUnmatchedMailResults(OutputStreamWriter outputStreamWriter) throws IOException {
        writeMailResults(this.unmatchedMailResults, outputStreamWriter);
        outputStreamWriter.flush();
    }

    private void writeMatchedMailResults(OutputStreamWriter outputStreamWriter) throws IOException {
        Map<String, MailProcessingRecord> writeResults = this.matchedMailResults; // keep current results for writing
        this.matchedMailResults = initMatchedMailResultContainer(); // establish new map for further unwritten results
        writeMailResults(writeResults, outputStreamWriter);
        outputStreamWriter.flush();
    }

    private void writeGeneralData(OutputStreamWriter outputStreamWriter) throws IOException {
        outputStreamWriter.write("start," + this.TimestampFirstResult + "," + new Date(this.TimestampFirstResult) + "\r\n");
        outputStreamWriter.write("end," + this.TimestampLastResult + "," + new Date(this.TimestampLastResult) + "\r\n");
        outputStreamWriter.write("current," + System.currentTimeMillis() + "," + new Date() + "\r\n");

        Iterator<String> iterator = this.environmentInfo.keySet().iterator();
        while (iterator.hasNext()) {
            String elementName = iterator.next();
            String elementValue = this.environmentInfo.get(elementName);
            outputStreamWriter.write(elementName + "," + elementValue + "\r\n");
        }
    }

    public long getTimestampFirstResult() {
        return this.TimestampFirstResult;
    }

    public long getTimestampLastResult() {
        return this.TimestampLastResult;
    }

    public void addError(int errorNumber, String errorMessage) {
        this.errors.add(new ErrorRecord(errorNumber, errorMessage));
    }

    public long getErrorCount() {
        return this.errors.size();
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
        List<JVMResourcesRecord> unwrittenResults = this.jvmStatistics;
        this.jvmStatistics = initMatchedJVMStatisticsResultContainer();
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

            List<ErrorRecord> unwrittenResults = this.errors;
            this.errors = initErrorResultContainer();
            Iterator<ErrorRecord> iterator = unwrittenResults.iterator();
            while (iterator.hasNext()) {
                ErrorRecord record = iterator.next();

                StringBuffer resultString = new StringBuffer();
                resultString.append(record.timestamp).append(",");
                resultString.append(record.number).append(",");
                resultString.append(record.message).append(",");
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
    long timestamp = -1;
    int number = -1;
    String message = null;

    public ErrorRecord(int number, String message) {
        this.timestamp = System.currentTimeMillis();
        this.number = number;
        this.message = message;
    }
}
