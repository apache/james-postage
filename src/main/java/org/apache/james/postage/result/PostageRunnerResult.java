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

import java.util.Map;

/**
 * collects and writes all result data for one scenario
 */
public interface PostageRunnerResult {

    void setEnvironmentDescription(Map descriptionItems);

    /**
     * add a record to be matched later on
     * @param mailProcessingRecord
     */
    void addNewMailRecord(MailProcessingRecord mailProcessingRecord);

    /**
     * retrieve the matching record, if existing
     * @param mailProcessingRecord record for whom a match is searched
     * @return null, if no match is found or matching and merged record otherwise
     */
    MailProcessingRecord matchMailRecord(MailProcessingRecord mailProcessingRecord);

    /**
     * count the valid matches
     * @param mailProcessingRecord
     */
    void recordValidatedMatch(MailProcessingRecord matchedAndMergedRecord);    
    
    void addJVMResult(JVMResourcesRecord jvmResourcesRecord);

    long getUnmatchedMails();

    long getMatchedMails();

    long getValidMails();

    long getTimestampFirstResult();

    long getTimestampLastResult();

    void addError(int errorNumber, String errorMessage);

    long getErrorCount();

    void writeResults(String filenameMailResults, String filenameJVMStatistics, String filenameErros, boolean flushMatchedMailOnly);
}
