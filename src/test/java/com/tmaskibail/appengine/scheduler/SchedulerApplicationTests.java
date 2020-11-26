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

import com.google.gson.Gson;
import com.tmaskibail.appengine.scheduler.model.Environment;
import com.tmaskibail.appengine.scheduler.model.Parameters;
import com.tmaskibail.appengine.scheduler.model.Payload;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.Assert.*;

class SchedulerApplicationTests {

    @Test
    void contextLoads() {
        Environment e = new Environment();
        e.setZone("us-central1-f");

        Parameters p = new Parameters();
        p.setBigQueryLoadingTemporaryDirectory("temp_dir");
        p.setInputFilePattern("../*.csv");
        p.setJavascriptTextTransformFunctionName("funcName");
        p.setJavascriptTextTransformGcsPath("gs://abc/pqr");
        p.setJSONPath("gs://json+path");
        p.setOutputTable("mm..mm..mm.");

        Payload payload = new Payload();
        payload.setJobName("job" + UUID.randomUUID());
        payload.setParameters(p);
        payload.setEnvironment(e);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload);

        assertNotNull(jsonPayload);
        assertTrue(jsonPayload.contains("temp_dir"));
    }

    @Test
    void test() {

        String json = "{\n" +
                "  \"job\": {\n" +
                "    \"id\": \"2020-11-18_02_48_41-4499587226046651394\",\n" +
                "    \"projectId\": \"tmaskibail-sandbox\"}}";

        JSONObject o = new JSONObject(json);
        assertEquals(o.getJSONObject("job").get("id"), "2020-11-18_02_48_41-4499587226046651394");
    }
}
