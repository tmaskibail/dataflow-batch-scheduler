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
package com.tmaskibail.appengine.scheduler.util;

import com.google.gson.Gson;
import com.tmaskibail.appengine.scheduler.model.Environment;
import com.tmaskibail.appengine.scheduler.model.Parameters;
import com.tmaskibail.appengine.scheduler.model.Payload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataflowTemplateUtil {

    @Value("${dataflow.javascript.function.name}")
    private String javascriptFunctionName;

    @Value("${dataflow.javascript.udf.file.path}")
    private String javascriptUdfFilePath;

    @Value("${dataflow.json.schema.path}")
    private String jsonSchemaPath;

    @Value("${dataflow.input.file.pattern}")
    private String inputFilePattern;

    @Value("${dataflow.output.bq.table}")
    private String outputBQTable;

    @Value("${dataflow.temp.directory}")
    private String tempDirectory;

    public String constructPayload(String jobId, String zone) {
        Environment environment = new Environment();
        environment.setZone(zone);

        Parameters parameters = new Parameters();
        parameters.setBigQueryLoadingTemporaryDirectory(tempDirectory);
        parameters.setInputFilePattern(inputFilePattern);
        parameters.setJavascriptTextTransformFunctionName(javascriptFunctionName);
        parameters.setJavascriptTextTransformGcsPath(javascriptUdfFilePath);
        parameters.setJSONPath(jsonSchemaPath);
        parameters.setOutputTable(outputBQTable);

        Payload payload = new Payload();
        payload.setJobName(jobId);
        payload.setParameters(parameters);
        payload.setEnvironment(environment);

        Gson gson = new Gson();
        return gson.toJson(payload);
    }
}
