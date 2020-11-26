/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tmaskibail.appengine.scheduler;

import com.tmaskibail.appengine.scheduler.process.DataflowProcessor;
import com.tmaskibail.appengine.scheduler.process.GcpFileMover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@SpringBootApplication
public class SchedulerApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerApplication.class);

    @Autowired
    private GcpFileMover gcpFileMover;

    @Autowired
    private DataflowProcessor dataflowProcessor;

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        scheduleBatchProcessingTask();
    }

    @Scheduled(fixedDelay = 30000)
    public void scheduleBatchProcessingTask() {
        LOG.info("Initiated pipeline execution at {}", new Date());
        // Move files of certain  pattern from input to processing directory
        if (gcpFileMover.moveBlobsToProcessing()) {
            // invoke dataflow batch job template & wait for it to complete
            if (dataflowProcessor.submitDataflowJob()) {
                // move files from processing folder to archive
                gcpFileMover.moveBlobsToArchive();
            }
        }
    }
}
