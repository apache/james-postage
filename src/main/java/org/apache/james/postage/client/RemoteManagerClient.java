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


package org.apache.james.postage.client;

import org.apache.commons.net.telnet.TelnetClient;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.Writer;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.BufferedReader;

public class RemoteManagerClient {

    private String  m_host = null;
    private int     m_port = -1;
    private String  m_username = null;
    private String  m_password = null;
    private TelnetClient m_telnetClient;
    private Reader m_reader;
    private Writer m_writer;

    public RemoteManagerClient(String host, int port, String username, String password) {
        m_host = host;
        m_port = port;
        m_username = username;
        m_password = password;
    }

    public boolean login() {
        m_telnetClient = new TelnetClient();
        try {
            m_telnetClient.connect(m_host, m_port);
            m_reader = new BufferedReader(new InputStreamReader(m_telnetClient.getInputStream()));
            m_writer = new OutputStreamWriter(m_telnetClient.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            sendCommand(m_username);
            sendCommand(m_password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List answers = readAnswer();
        if (answers == null || answers.size() == 0 || !((String)answers.get(answers.size() - 1)).startsWith("Welcome"))
        {
            disconnect();
            return false;
        }
        return true;
    }
    
    public void disconnect() {
        if (m_telnetClient == null) return;
        try {
            m_telnetClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            m_telnetClient = null;
            m_reader = null;
            m_writer = null;
        }
    }

    public List executeCommand(String command) {
        try {
            sendCommand(command);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List list = readAnswer();
        return list;
    }

    protected void sendCommand(String command) throws IOException {
        m_writer.write(command + "\n");
        m_writer.flush();
    }

    public List readAnswer() {
        long startTime = System.currentTimeMillis();
        try {
            while (!m_reader.ready() && startTime + 100 > System.currentTimeMillis()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (!m_reader.ready()) return null;
        } catch (IOException e) {
            return null;
        }

        StringBuffer stringBuffer = new StringBuffer();
        char[] charBuffer = new char[100];
        List allAnswerLines = new ArrayList();
        try {
            int readCount;
            while ((m_reader.ready() && (readCount = m_reader.read(charBuffer)) > 0)) {
                stringBuffer.append(charBuffer, 0, readCount);
                if (m_reader.ready()) continue;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        allAnswerLines.addAll(Arrays.asList(stringBuffer.toString().split("\n")));
        return allAnswerLines;
    }


}
