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


package org.apache.james.postage.jmx;

import java.io.IOException;
import java.util.Iterator;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.james.postage.SamplingException;
import org.apache.james.postage.execution.Sampler;
import org.apache.james.postage.result.JVMResourcesRecord;
import org.apache.james.postage.result.PostageRunnerResult;

/**
 * the peek into the James JVM is done using the build-in management (JMX) of J2SE 5 (and probably later)
 * you must start James under a J2SE 5 compatible JVM and
 * add some system properties to the phoenix.[sh|bat] file (all on one line):<br/>
 * <br/>
 * <code>
 * JVM_OPTS="-Djava.ext.dirs=$JVM_EXT_DIRS
 *           -Dcom.sun.management.jmxremote
 *           -Dcom.sun.management.jmxremote.ssl=false
 *           -Dcom.sun.management.jmxremote.authenticate=false
 *           -Dcom.sun.management.jmxremote.port=10201 "
 * </code>
 * <br/>
 * this class does not even compile on Java versions before JSE 5.<br/>
 */
public class JVMResourceSampler implements Sampler {
    private String host;
    private int port;
    private PostageRunnerResult results;
    private MBeanServerConnection mBeanServerConnection;

    public JVMResourceSampler(String host, int port, PostageRunnerResult results) {
        this.host = host;
        this.port = port;
        this.results = results;
    }

    public void connectRemoteJamesJMXServer() throws SamplingException {
        String serviceURL = "service:jmx:rmi:///jndi/rmi://" + this.host + ":" + this.port + "/jmxrmi";
        try {
            JMXServiceURL jmxServiceURL = new JMXServiceURL(serviceURL);
            JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
            this.mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        } catch (IOException e) {
            throw new SamplingException("could not connect to " + serviceURL, e);
        }
    }

    private void takeMemorySample(JVMResourcesRecord jvmResourcesRecord) throws SamplingException {
        CompositeDataSupport data = null;
        data = getRemoteAttributeValue("java.lang:type=Memory", "HeapMemoryUsage");
        jvmResourcesRecord.setMemoryCommitted(((Long)data.get("committed")).longValue());
        jvmResourcesRecord.setMemoryInit(((Long)data.get("init")).longValue());
        jvmResourcesRecord.setMemoryMax(((Long)data.get("max")).longValue());
        jvmResourcesRecord.setMemoryUsed(((Long)data.get("used")).longValue());
    }

    private void takeThreadingSample(JVMResourcesRecord jvmResourcesRecord) throws SamplingException {
        AttributeList attributes = getRemoteThreadingAttributeValues();
        jvmResourcesRecord.setThreadCountCurrent(((Integer)getAttributeValue(attributes, "ThreadCount")).longValue());
        jvmResourcesRecord.setThreadCountPeak(((Integer)getAttributeValue(attributes, "PeakThreadCount")).longValue());
        jvmResourcesRecord.setThreadCountTotalStarted(((Long)getAttributeValue(attributes, "TotalStartedThreadCount")).longValue());
    }

    private CompositeDataSupport getRemoteAttributeValue(String jmxObjectName, String attributeName) throws SamplingException {
        CompositeDataSupport data;
        try {
            ObjectName name = new ObjectName(jmxObjectName);
            data = (CompositeDataSupport)this.mBeanServerConnection.getAttribute(name, attributeName);
        } catch (IOException e) {
            throw new SamplingException("lost connection to JMX server", e);
        } catch (Exception e) {
            throw new SamplingException("failed to take memory sample", e);
        }
        return data;
    }

    private AttributeList getRemoteThreadingAttributeValues() throws SamplingException {
        try {
            ObjectName name = new ObjectName("java.lang:type=Threading");
            String[] attributeNames = new String[] {"PeakThreadCount", "ThreadCount", "TotalStartedThreadCount"};
            AttributeList attributes = this.mBeanServerConnection.getAttributes(name, attributeNames);
            return attributes;
        } catch (IOException e) {
            throw new SamplingException("lost connection to JMX server", e);
        } catch (Exception e) {
            throw new SamplingException("failed to take memory sample", e);
        }
    }

    private Object getAttributeValue(AttributeList attributeList, String key) {
        for (Iterator<?> iterator = attributeList.iterator(); iterator.hasNext();) {
            Attribute attribute = (Attribute)iterator.next();
            if (attribute.getName().equals(key)) return attribute.getValue();
        }
        return null;
    }

    public void doSample() throws SamplingException {
        JVMResourcesRecord jvmResourcesRecord = new JVMResourcesRecord();
        takeMemorySample(jvmResourcesRecord);
        takeThreadingSample(jvmResourcesRecord);
        this.results.addJVMResult(jvmResourcesRecord);
    }
}
