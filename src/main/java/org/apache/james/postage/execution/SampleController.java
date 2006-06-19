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


package org.apache.james.postage.execution;

import org.apache.james.postage.SamplingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * initiate one shot of sample data generation 
 */
public class SampleController extends TimerTask {

    private static Log log = LogFactory.getLog(SampleController.class);

    private Sampler m_sampler;
    private int m_samplesPerMinute;
    private Timer m_timer;
    private int m_secondsDelayOnStop = 0;

    public SampleController(Sampler sampler, int samplesPerMinute) {
        m_sampler = sampler;
        m_samplesPerMinute = samplesPerMinute;
    }

    public SampleController(Sampler sampler, int samplesPerMinute, int secondsDelayOnStop) {
        this(sampler, samplesPerMinute);
        m_secondsDelayOnStop = secondsDelayOnStop;
    }

    public void runThreaded() {
        if (m_samplesPerMinute < 1) {
            log.warn("sample controller effectivly disabled with sample-per-minute value = " + m_samplesPerMinute);
            return;
        }
        m_timer = new Timer(true);
        m_timer.schedule(this, 5, 60*1000/m_samplesPerMinute);
    }

    public void stop() {
        if (m_timer != null) m_timer.cancel();

        if (m_secondsDelayOnStop > 0) {
            try {
                log.warn("sampler is waiting additional seconds: " + m_secondsDelayOnStop + " (type = " + m_sampler.getClass().getName() + ")");
                Thread.sleep(m_secondsDelayOnStop * 1000);
                log.warn("sampler delay passed.");
            } catch (InterruptedException e) {
                ; // fall thru
            }
        }
    }

    public void run() {
        try {
            m_sampler.doSample();
        } catch (SamplingException e) {
            log.warn("taking sample failed", e);
        }
    }
}
