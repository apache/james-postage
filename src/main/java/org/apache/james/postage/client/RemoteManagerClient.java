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


package org.apache.james.postage.client;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Apache James Server specific client to access its Remote Manager.<br/>
 * This is used for creating user accounts.
 */
public class RemoteManagerClient {

    private String  m_host = null;
    private int     m_port = -1;
    private String  m_username = null;
    private String  m_password = null;
    private TelnetClient m_telnetClient;
    private BufferedReader m_reader;
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
            m_reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(m_telnetClient.getInputStream(), 1024), "ASCII"));
            m_writer = new OutputStreamWriter(m_telnetClient.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            sendCommand(m_username);
            delay();
            sendCommand(m_password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> answers = readAnswer();
        if (answers == null || answers.size() == 0 || !answers.get(answers.size() - 1).startsWith("Welcome"))
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

    public List<String> executeCommand(String command) {
        try {
            sendCommand(command);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<String> list = readAnswer();
        return list;
    }

    protected void sendCommand(String command) throws IOException {
        m_writer.write(command + "\n");
        m_writer.flush();
    }

    public List<String> readAnswer() {
        return readAnswer(0);
    }

    protected List<String> readAnswer(int numLines) {
        List<String> allAnswerLines = new ArrayList<String>();
        try {
            delay();
            if (numLines > 0) {
                for (int i = 0; i < numLines; i++) {
                    readline(allAnswerLines);
                }
            } else {
                readline(allAnswerLines);

                while (m_reader.ready()) {
                    while (m_reader.ready()) {
                        readline(allAnswerLines);
                    }
                    delay();
                }
            }
            return allAnswerLines;
        } catch (NullPointerException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    private void readline(List<String> allAnswerLines) throws IOException {
        if (!m_reader.ready()) return;
        String line = m_reader.readLine();
        if (line != null) allAnswerLines.add(line);
    }

    private void delay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }


}
