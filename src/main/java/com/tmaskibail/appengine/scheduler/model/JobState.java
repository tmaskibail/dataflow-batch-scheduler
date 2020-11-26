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
package com.tmaskibail.appengine.scheduler.model;

/**
 * Use google.dataflow.v1beta3.Job instead
 * https://cloud.google.com/dataflow/docs/reference/rest/v1b3/projects.jobs#Job.JobState
 */
public enum JobState {

    JOB_STATE_DONE("JOB_STATE_DONE"),
    JOB_STATE_FAILED("JOB_STATE_FAILED"),
    JOB_STATE_CANCELLED("JOB_STATE_CANCELLED"),
    JOB_STATE_UNKNOWN("JOB_STATE_UNKNOWN"),
    JOB_STATE_DRAINING("JOB_STATE_DRAINING"),
    JOB_STATE_DRAINED("JOB_STATE_DRAINED"),
    JOB_STATE_QUEUED("JOB_STATE_QUEUED"),
    JOB_STATE_PENDING("JOB_STATE_PENDING");

    public final String state;

    JobState(String state) {
        this.state = state;
    }
}
