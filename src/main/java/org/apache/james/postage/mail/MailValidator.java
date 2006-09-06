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
package org.apache.james.postage.mail;

import javax.mail.Message;

import org.apache.james.postage.result.MailProcessingRecord;

/**
 * implementations validate matched and received messages by checking for the expected properties.
 * for example, they could check for spam-marking headers or mime parts.
 * most commonly, each MailValidator implementation matches one corresponding MailFactory.
 */
public interface MailValidator {
	public boolean validate(Message message, MailProcessingRecord record);
}
