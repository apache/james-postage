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

    private Sampler sampler;
    private int samplesPerMinute;
    private Timer timer;
    private int secondsDelayOnStop = 0;

    public SampleController(Sampler sampler, int samplesPerMinute) {
        this.sampler = sampler;
        this.samplesPerMinute = samplesPerMinute;
    }

    public SampleController(Sampler sampler, int samplesPerMinute, int secondsDelayOnStop) {
        this(sampler, samplesPerMinute);
        this.secondsDelayOnStop = secondsDelayOnStop;
    }

    public void runThreaded() {
        if (this.samplesPerMinute < 1) {
            log.warn("sample controller effectivly disabled with sample-per-minute value = " + this.samplesPerMinute);
            return;
        }
        this.timer = new Timer(true);
        this.timer.schedule(this, 5, 60*1000/this.samplesPerMinute);
    }

    public void stop() {
        if (this.timer != null) this.timer.cancel();

        if (this.secondsDelayOnStop > 0) {
            try {
                log.warn("sampler is waiting additional seconds: " + this.secondsDelayOnStop + " (type = " + this.sampler.getClass().getName() + ")");
                Thread.sleep(this.secondsDelayOnStop * 1000);
                log.warn("sampler delay passed.");
            } catch (InterruptedException e) {
                ; // fall thru
            }
        }
    }

    public void run() {
        try {
            this.sampler.doSample();
        } catch (SamplingException e) {
            log.warn("taking sample failed", e);
        }
    }
}
