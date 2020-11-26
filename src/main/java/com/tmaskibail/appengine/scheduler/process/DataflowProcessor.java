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
package com.tmaskibail.appengine.scheduler.process;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dataflow.DataflowScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.tmaskibail.appengine.scheduler.model.JobState;
import com.tmaskibail.appengine.scheduler.util.CustomJsonHttpContent;
import com.tmaskibail.appengine.scheduler.util.DataflowTemplateUtil;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class DataflowProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DataflowProcessor.class);

    @Autowired
    private DataflowTemplateUtil dataflowTemplateUtil;

    @Value("${service.account.key.file}")
    private String saKeyFile;

    @Value("${project.id}")
    private String projectId;

    @Value("${dataflow.template.path}")
    private String dataflowTemplatePath;

    @Value("${dataflow.rest.uri}")
    private String restUri;

    @Value("${dataflow.environment.zone}")
    private String environmentZone;

    public boolean submitDataflowJob() {
        String jobName = "job" + new Random().nextLong();
        LOG.info("Initiating a Dataflow template job with name [{}]", jobName);
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(saKeyFile))
                    // CAUTION: This is for demo purposes only! Use granular scopes.
                    .createScoped(DataflowScopes.CLOUD_PLATFORM);

            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(new HttpCredentialsAdapter(credentials));

            GenericUrl url = new GenericUrl(constructPostUri());
            CustomJsonHttpContent jsonHttpContent = new CustomJsonHttpContent(new JacksonFactory(),
                    dataflowTemplateUtil.constructPayload(jobName, environmentZone));
            HttpRequest request = requestFactory.buildPostRequest(url, jsonHttpContent);
            HttpResponse response = request.execute();
            JSONObject responseJobObject = new JSONObject(response.parseAsString());
            String jobId = (String) responseJobObject.getJSONObject("job").get("id");
            if (response.getStatusCode() != HttpStatus.SC_OK) {
                LOG.error("Job submission failed! Response code {},  Reason : {}", response.getStatusCode(), response.getStatusMessage());
            }
            // Wait for the batch job to launch
            waitForJobToComplete(requestFactory, jobId);
        } catch (IOException | GeneralSecurityException | InterruptedException e) {
            LOG.error("Error occurred while submitting Dataflow batch job ", e);
            return false;
        }
        return true;
    }

    private String constructPostUri() {
        return restUri + projectId + "/templates:launch?gcsPath=" + dataflowTemplatePath;
    }

    public void waitForJobToComplete(HttpRequestFactory requestFactory, String jobId) throws InterruptedException, IOException {
        HttpRequest request;
        HttpResponse response;

        // Sleep for 4 minutes before querying for the status.
        //This is arbitrary value and has been introduced to stop querying the status unnecessarily!
        TimeUnit.MINUTES.sleep(4);

        // Check JOB Status and wait until its JOB_STATE_DONE
        JobState jobState = JobState.JOB_STATE_QUEUED;
        while ((!jobState.equals(JobState.JOB_STATE_DONE))
                && (!jobState.equals(JobState.JOB_STATE_CANCELLED))
                && (!jobState.equals(JobState.JOB_STATE_FAILED))) {
            // Query once every minute
            TimeUnit.MINUTES.sleep(1);
            GenericUrl getJobStatusUrl = new GenericUrl(constructGetUri(jobId));
            request = requestFactory.buildGetRequest(getJobStatusUrl);
            response = request.execute();
            JSONObject jobStatusJsonObject = new JSONObject(response.parseAsString());
            jobState = JobState.valueOf(jobStatusJsonObject.getString("currentState"));
            LOG.info("Querying job id [{}] with name [{}] for status. The current job status is [{}]", jobId, jobStatusJsonObject.getString("name"), jobState.name());
        }

        LOG.info("Dataflow template job step completed");
    }

    private String constructGetUri(String jobId) {
        return restUri + projectId + "/locations/" + environmentZone + "/jobs/" + jobId + "?view=JOB_VIEW_SUMMARY";
    }
}
